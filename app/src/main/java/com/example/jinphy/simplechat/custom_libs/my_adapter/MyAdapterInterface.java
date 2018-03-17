package com.example.jinphy.simplechat.custom_libs.my_adapter;

import android.widget.CompoundButton;

import java.util.List;

/**
 * DESC:
 * Created by jinphy on 2018/3/17.
 */

public interface MyAdapterInterface<T> {


    MyAdapterInterface<T> onCreateView(MyAdapter.OnCreateView onCreateView);

    MyAdapterInterface<T> onBindView(MyAdapter.OnBindView<T> onBindView);

    MyAdapterInterface<T> onGetItemViewType(MyAdapter.OnGetItemViewType<T> onGetItemViewType);

    MyAdapterInterface<T> onInflate(MyAdapter.OnInflate onInflate);

    MyAdapterInterface<T> onClick(MyAdapter.OnClickListener<T> onclick);

    MyAdapterInterface<T> onLongClick(MyAdapter.OnLongClickListener<T> onLongClick);

    MyAdapterInterface<T> onCheck(MyAdapter.OnCheckChangedListener<T> onCheck);


    MyAdapterInterface<T> data(List<T> data);


    MyAdapter<T> make();




}
