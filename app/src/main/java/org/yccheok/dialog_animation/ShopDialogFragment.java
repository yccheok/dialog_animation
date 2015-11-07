package org.yccheok.dialog_animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

/**
 * Created by yccheok on 7/11/2015.
 */
public class ShopDialogFragment extends SherlockDialogFragment {

    private View selectedRowView;
    private View selectedDescriptionView;
    private int position = -1;

    public static ShopDialogFragment newInstance() {
        ShopDialogFragment shopDialogFragment = new ShopDialogFragment();
        return shopDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentManager fm = this.getFragmentManager();

        Bundle bundle = this.getArguments();

        final Activity activity = getActivity();

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View view = inflater.inflate(R.layout.shop_dialog_fragment, null);


        final LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.scroll_view_linear_layout);

        int key = 0;
        String[] titles = {"Title 1", "Title 2"};
        String[] descriptions = {
            "Title 1 : The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.",
            "Title 2 : The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog"
        };

        View firstRowView = null;
        View firstDescriptionLinearLayout = null;

        for (int i=0; i<2; i++) {
            final View rowView = inflater.inflate(R.layout.shop_row_layout, linearLayout, false);
            linearLayout.addView(rowView);

            final TextView itemTextView = (TextView)rowView.findViewById(R.id.item_text_view);
            final TextView priceTextView = (TextView)rowView.findViewById(R.id.price_text_view);
            final LinearLayout descriptionLinearLayout = (LinearLayout)rowView.findViewById(R.id.description_linear_layout);
            final TextView descriptionTextView = (TextView)descriptionLinearLayout.findViewById(R.id.description_text_view);

            if (firstRowView == null) {
                firstRowView = rowView;
            }

            if (firstDescriptionLinearLayout == null) {
                firstDescriptionLinearLayout = descriptionLinearLayout;
            }

            itemTextView.setText(titles[i]);
            priceTextView.setText("0.99");
            descriptionTextView.setText(descriptions[i]);

            final int pos = i;

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == pos) {
                        return;
                    }

                    position = pos;

                    // Highlight
                    rowView.setSelected(true);

                    if (selectedRowView != null) {
                        selectedRowView.setSelected(false);
                    }

                    if (selectedDescriptionView != null) {
                        animateCollapsing(selectedDescriptionView);
                    }

                    animateExpanding(descriptionLinearLayout);

                    selectedRowView = rowView;
                    selectedDescriptionView = descriptionLinearLayout;
                }
            });
        }

        firstRowView.setSelected(true);
        firstDescriptionLinearLayout.setVisibility(View.VISIBLE);
        selectedRowView = firstRowView;
        selectedDescriptionView = firstDescriptionLinearLayout;

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            // Remove black padding and border.
            builder.setInverseBackgroundForced(true);
        }

        final AlertDialog dialog = builder.setView(view).create();

        dialog.setCanceledOnTouchOutside(true);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            // Remove black padding and border.
            dialog.setView(view, 0, 0, 0, 0);
        }

        final ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = view.getViewTreeObserver();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }

                // http://stackoverflow.com/questions/19326142/why-listview-expand-collapse-animation-appears-much-slower-in-dialogfragment-tha
                int width = dialog.getWindow().getDecorView().getWidth();
                int height = dialog.getWindow().getDecorView().getHeight();
                dialog.getWindow().setLayout(width, height);
            }
        });

        return dialog;
    }

    public static ValueAnimator createHeightAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;

                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void animateCollapsing(final View view) {
        int origHeight = view.getHeight();

        ValueAnimator animator = createHeightAnimator(view, origHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

    private void animateExpanding(final View view) {
        view.setVisibility(View.VISIBLE);

        // http://stackoverflow.com/questions/19255446/accurate-way-to-determine-the-measured-height-of-a-view-even-before-it-changed
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(((View) view.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(1024, View.MeasureSpec.AT_MOST);
        view.measure(widthSpec, heightSpec);

        ValueAnimator animator = createHeightAnimator(view, 0, view.getMeasuredHeight());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        animator.start();
    }

}
