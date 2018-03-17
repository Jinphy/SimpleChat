package com.example.jinphy.simplechat.custom_libs.my_adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jinphy.simplechat.base.BaseAdapter;

import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public class MyAdapter<T> extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements MyAdapterInterface<T> {

    /**
     * DESC: 创建View是的回调，通过传入的holder为需要的View findViewById
     * Created by jinphy, on 2018/3/17, at 19:07
     */
    private OnCreateView onCreateView;

    /**
     * DESC: 数据绑定View时回调，通过传入的数据实体类设置View
     * Created by jinphy, on 2018/3/17, at 19:08
     */
    private OnBindView<T> onBindView;

    /**
     * DESC: 当需要根据不同的数据设置不同的布局时回调，以返回对应的布局类型
     * Created by jinphy, on 2018/3/17, at 19:09
     */
    private OnGetItemViewType<T> onGetItemViewType;

    /**
     * DESC: 加载布局时回调，根据传入的View的布局类型（viewType）返回对应的布局id
     * Created by jinphy, on 2018/3/17, at 19:10
     */
    private OnInflate onInflate;

    /**
     * DESC: 设置点击监听器
     * Created by jinphy, on 2018/3/17, at 19:11
     */
    protected OnClickListener<T> onClick;

    /**
     * DESC: 设置长按监听器
     * Created by jinphy, on 2018/3/17, at 19:11
     */
    protected OnLongClickListener<T> onLongClick;


    /**
     * DESC: 设置checkBox的监听
     * Created by jinphy, on 2018/3/17, at 19:33
     */
    protected OnCheckChangedListener<T> onCheck;


    /**
     * DESC: 数据集
     * Created by jinphy, on 2018/3/17, at 19:11
     */
    protected List<T> data;


    /**
     * DESC: 获取一个adapter实例
     * Created by jinphy, on 2018/3/17, at 19:12
     */
    public static <U> MyAdapterInterface<U> newInstance() {
        MyAdapter<U> adapter = new MyAdapter<>();
        return adapter;
    }

    /**
     * DESC: 私有化构造函数
     * Created by jinphy, on 2018/3/17, at 19:12
     */
    private MyAdapter(){
        data = new LinkedList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(onInflate.call(viewType), parent, false);

        ViewHolder holder = new ViewHolder(itemView);
        onCreateView.call(holder);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = data.get(position);

        // 回调设置View
        this.onBindView.call(holder, item, position);

        // 设置点击监听
        int viewType = getItemViewType(position);
        if (onClick != null && holder.clickedViews != null) {
            for (View view : holder.clickedViews) {
                view.setOnClickListener(v -> onClick.onClick(v, item, holder, viewType, position));
            }
        }

        // 设置长按监听
        if (onLongClick != null && holder.longClickedViews != null) {
            for (View view : holder.longClickedViews) {
                view.setOnLongClickListener(v ->
                        onLongClick.onLongClick(v, item, holder, viewType, position));
            }
        }

        if (onCheck != null && holder.checkedBoxs != null) {
            for (CheckBox checkedBox : holder.checkedBoxs) {
                checkedBox.setOnCheckedChangeListener((view, isChecked) -> {
                    onCheck.onCheck(checkedBox, item, holder, viewType, position);
                });
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (onGetItemViewType != null) {
            return onGetItemViewType.call(data.get(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     * DESC: 获取所有的数据
     * Created by jinphy, on 2018/3/17, at 19:50
     */
    public List<T> getData() {
        return data;
    }

    /**
     * DESC: 获取过滤后的数据
     * Created by jinphy, on 2018/3/17, at 19:51
     */
    public List<T> getData(BaseAdapter.Filter<T> filter) {
        if (data == null || data.size() == 0 || filter == null) {
            return data;
        }
        List<T> result = new LinkedList<>();
        for (T item : data) {
            if (filter.filter(item)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public MyAdapterInterface<T> onCreateView(OnCreateView onCreateView) {
        this.onCreateView = onCreateView;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onBindView(OnBindView<T> onBindView) {
        this.onBindView = onBindView;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onGetItemViewType(OnGetItemViewType<T> onGetItemViewType) {
        this.onGetItemViewType = onGetItemViewType;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onInflate(OnInflate onInflate) {
        this.onInflate = onInflate;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onClick(OnClickListener<T> onclick) {
        this.onClick = onclick;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onLongClick(OnLongClickListener<T> onLongClick) {
        this.onLongClick = onLongClick;
        return this;
    }

    @Override
    public MyAdapterInterface<T> onCheck(OnCheckChangedListener<T> onCheck) {
        this.onCheck = onCheck;
        return this;
    }

    @Override
    public MyAdapterInterface<T> data(List<T> data) {
        this.data.clear();
        if (data != null && data.size() > 0) {
            this.data.addAll(data);
        }
        return this;
    }

    @Override
    public MyAdapter<T> make() {
        return this;
    }

    public void update(List<T> data) {
        data(data);
        notifyDataSetChanged();
    }

    //-------------------------------------------------------------------------------------

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView[] imageView;

        public TextView[] textView;

        public CheckBox[] checkBox;

        public CircleImageView[] circleImageView;

        public View[] view;

        public final View item;

        /**
         * DESC: 设置了点击监听器的view
         * Created by jinphy, on 2018/3/17, at 19:34
         */
        private View[] clickedViews;

        /**
         * DESC: 设置了长按监听器的view
         * Created by jinphy, on 2018/3/17, at 19:34
         */
        private View[] longClickedViews;

        /**
         * DESC: 设置的监听器的CheckBox
         * Created by jinphy, on 2018/3/17, at 19:33
         */
        private CheckBox[] checkedBoxs;

        public ViewHolder(View itemView) {
            super(itemView);
            this.item = itemView;
        }

        /**
         * DESC: 调用该方法添加需要设置点击监听的view
         * Created by jinphy, on 2018/3/17, at 19:35
         */
        public void setClickedViews(View... clickViews) {
            this.clickedViews = clickViews;
        }

        /**
         * DESC: 调用该方法添加需要设置长按监听的view
         * Created by jinphy, on 2018/3/17, at 19:35
         */
        public void setLongClickedViews(View... longClickViews) {
            this.longClickedViews = longClickViews;
        }

        /**
         * DESC: 调用该方法添加需要设置check监听的CheckBox
         * Created by jinphy, on 2018/3/17, at 19:36
         */
        public void setCheckedBoxs(CheckBox... checkedBoxs) {
            this.checkedBoxs = checkedBoxs;
        }
    }

    //------------------------------------------------------------------------

    public interface OnCreateView{

        void call(ViewHolder holder);
    }

    public interface OnBindView<T>{
        void call(ViewHolder holder, T item, int position);
    }

    public interface OnGetItemViewType<T>{

        int call(T item);
    }

    public interface OnInflate {
        int call(int viewType);
    }

    public interface OnClickListener<T> {

        void onClick(View v, T item, ViewHolder holder, int type, int position);
    }

    public interface OnCheckChangedListener<T>{

        void onCheck(CheckBox checkBox, T item, ViewHolder holder, int type, int position);
    }

    public interface OnLongClickListener<T> {

        boolean onLongClick(View v, T item, ViewHolder holder, int type,int position);
    }
}
