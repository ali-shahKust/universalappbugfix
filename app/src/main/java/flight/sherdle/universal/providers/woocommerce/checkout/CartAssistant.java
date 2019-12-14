package flight.sherdle.universal.providers.woocommerce.checkout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import flight.sherdle.universal.Config;
import flight.sherdle.universal.HolderActivity;
import flight.sherdle.universal.R;
import flight.sherdle.universal.providers.woocommerce.WooCommerceTask;
import flight.sherdle.universal.providers.woocommerce.model.products.Attribute;
import flight.sherdle.universal.providers.woocommerce.model.products.Product;
import flight.sherdle.universal.providers.woocommerce.ui.CartFragment;

import java.util.ArrayList;
import java.util.List;

public class CartAssistant {

    private Cart mCart;
    private Context mContext;
    private View mCartButton;
    private Product mProduct;

    private ArrayList<Product> variations;

    /**
     * An assistant for handling the process between a press on the buy button and
     * adding to cart. Keeping mind stock checking, variations and informing the user.
     * @param context Context
     * @param cartButton Button that has been pressed
     * @param product Product to add
     */
    public CartAssistant(Context context, View cartButton, Product product) {
        this.mCart = Cart.getInstance(context);
        this.mContext = context;
        this.mCartButton = cartButton;
        this.mProduct = product;
    }

    public void addProductToCart(Product variation) {
        if (mProduct.getExternalUrl() != null && !mProduct.getExternalUrl().isEmpty()) {
            HolderActivity.startWebViewActivity(mContext, mProduct.getExternalUrl(), Config.OPEN_EXPLICIT_EXTERNAL, false, null);
        } else if (mProduct.getType().equals("variable") && variation == null) {
            retrieveVariations();
        } else {
            boolean success = mCart.addProductToCart(mProduct, variation);
            int resID = success ? R.string.cart_success : R.string.out_of_stock;
            Snackbar bar = Snackbar.make(mCartButton, resID, Snackbar.LENGTH_LONG)
                    .setAction(R.string.view_cart, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HolderActivity.startActivity(mContext, CartFragment.class);
                        }
                    });
            bar.show();
            ((TextView) bar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).
                    setTextColor(mContext.getResources().getColor(R.color.white));
        }
    }

    private void retrieveVariations() {
        if (variations == null) {
            final ProgressDialog progress = ProgressDialog.show(mContext,
                    mContext.getResources().getString(R.string.loading),
                    mContext.getResources().getString(R.string.loading), true);

            WooCommerceTask.Callback<Product> callback =
                    new WooCommerceTask.Callback<Product>() {
                @Override
                public void success(ArrayList<Product> productList) {
                    progress.dismiss();
                    variations = productList;
                    selectVariation();
                }

                @Override
                public void failed() {
                    progress.dismiss();
                    Toast.makeText(mContext, R.string.varations_missing, Toast.LENGTH_SHORT).show();
                }
            };

            new WooCommerceTask.WooCommerceBuilder(mContext)
                    .getVariationsForProduct(callback, mProduct.getId()).execute();

        } else {
            selectVariation();
        }
    }

    private void selectVariation() {
        ArrayList<String> items = new ArrayList<>();
        for (Product variation : variations) {
            String item = getVariationDescription(variation);
            item += " (" + PriceFormat.formatPrice(getPrice(mProduct, variation)) + ")";
            items.add(item);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String[] arr = items.toArray(new String[0]);
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addProductToCart(variations.get(i));
            }
        });
        if (variations.size() == 0)
            builder.setMessage(R.string.out_of_stock);

        builder.setTitle(R.string.varations);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getVariationDescription(Product variation){
        List<String> attributes = new ArrayList<String>();
        for (Attribute attribute : variation.getAttributes()){
            attributes.add(attribute.getName() + ": " + attribute.getOption());
        }
        return TextUtils.join(", ", attributes);
    }

    public static float getPrice(Product product, Product variation){
        if (variation == null || variation.getPrice() == 0){
            return product.getPrice();
        } else {
            if (variation.getOnSale())
                return variation.getSalePrice();
            else
                return variation.getPrice();
        }
    }
}