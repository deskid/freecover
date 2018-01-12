package com.github.deskid.demo;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.deskid.freecover.FreeCover;
import com.github.deskid.freecover.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DemoActivity extends Activity {

    @BindView(R.id.items)
    RecyclerView mRecyclerView;

    @BindView(R.id.spinnerStyle)
    Spinner mSpinnerStyle;

    @BindView(R.id.spinnerPosition)
    Spinner mSpinnerPostion;

    int mPosition = 4;

    @FreeCover.Anchor
    private String mAnchor = FreeCover.TOP;
    @FreeCover.HoleStyle
    private String mStyle = FreeCover.CIRCLE;

    boolean mIsInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        ButterKnife.bind(this);

        initStyleSpinner();

        initAnchorSpinner();

        ItemAdapter itemAdapter = new ItemAdapter();
        mRecyclerView.setAdapter(itemAdapter);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int space = ScreenUtils.dpToPx(8);
                if (parent.getChildAdapterPosition(view) % 3 == 0) {
                    outRect.left = space;
                    outRect.top = space;
                    outRect.right = space / 2;
                    outRect.bottom = space;
                } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                    outRect.left = space / 2;
                    outRect.top = space;
                    outRect.right = space / 2;
                    outRect.bottom = space;
                } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                    outRect.left = space / 2;
                    outRect.top = space;
                    outRect.right = space;
                    outRect.bottom = space;
                }

            }
        });
        mRecyclerView.setLayoutManager(layoutManager);

    }

    private void initAnchorSpinner() {
        String[] position = {"TOP", "BOTTOM", "LEFT", "RIGHT", "TOP_CENTER", "BOTTOM_CENTER"};
        final String[] anchors = {
                FreeCover.TOP,
                FreeCover.BOTTOM,
                FreeCover.LEFT,
                FreeCover.RIGHT,
                FreeCover.TOP_CENTER,
                FreeCover.BOTTOM_CENTER
        };

        mSpinnerPostion.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, position));
        mSpinnerPostion.setOnItemSelectedListener(new SimpleOnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAnchor = anchors[position];
                if (!mIsInit) {
                    onGuideCoverViewConfigChange(mStyle, mAnchor, mPosition);
                }
                mIsInit = false;
            }
        });
    }

    private void initStyleSpinner() {
        String[] style = {"CIRCLE", "RECTANGLE", "ROUNDRECT", "OVAL"};
        final String[] styles = {
                FreeCover.CIRCLE,
                FreeCover.RECTANGLE,
                FreeCover.ROUNDRECT,
                FreeCover.OVAL
        };

        mSpinnerStyle.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, style));
        mSpinnerStyle.setOnItemSelectedListener(
                new SimpleOnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mStyle = styles[position];
                        onGuideCoverViewConfigChange(mStyle, mAnchor, mPosition);
                    }
                }
        );
    }

    private void onGuideCoverViewConfigChange(String style, String anchor, int position) {
        final FreeCover.ImageCoverBuilder imageCoverBuilder = new FreeCover.ImageCoverBuilder()
                .setAnchor(anchor)
                .setTopOffset(0)
                .setBottomOffset(0)
                .setLeftOffset(0)
                .setRightOffset(0)
                .setWidth(100)
                .setHeight(100)
                .setImgRes(R.drawable.ic_arrow_downward_black_24dp);

        FreeCover.init(this, mRecyclerView.getChildAt(position))
                .setHoleStyle(style)
                .setImageCover(imageCoverBuilder)
                .setupView();
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        int[] colors = getResources().getIntArray(R.array.colors);

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            int width = ScreenUtils.getScreenWidth() / 3 - ScreenUtils.dpToPx(16);
            int height = ScreenUtils.dpToPx(130);
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            parent.addView(imageView);
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mImageView.setBackgroundColor(colors[position]);
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (mPosition != holder.getAdapterPosition()) {
                        mPosition = holder.getAdapterPosition();
                        onGuideCoverViewConfigChange(mStyle, mAnchor, mPosition);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return colors.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;

            ViewHolder(final ImageView itemView) {
                super(itemView);
                mImageView = itemView;
            }
        }
    }

}
