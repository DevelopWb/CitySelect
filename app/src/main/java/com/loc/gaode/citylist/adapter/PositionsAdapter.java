package com.loc.gaode.citylist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.loc.gaode.R;

import java.util.ArrayList;

/**
 * Created by ${王sir} on 2017/8/10.
 * application
 */

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PoiItem> arrays;
    PositionLLCallBack callBack;


    public PositionsAdapter(Context context) {
        this.context = context;

    }
    public void setAdapterData(ArrayList<PoiItem> arrays){
        this.arrays = arrays;
        notifyDataSetChanged();
    }

    @Override
    public PositionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poi_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PositionsAdapter.ViewHolder holder, int position) {
      final  PoiItem poiItem = arrays.get(position);
        holder.addr_name .setText(poiItem.getTitle());
        holder.addr_info .setText(poiItem.getCityName()+poiItem.getSnippet());
        holder.position_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack!=null) {
                    callBack.positionOnClick(poiItem.getCityName()+poiItem.getSnippet());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrays==null?0:arrays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView addr_name;
        TextView addr_info;
        LinearLayout position_ll;

        public ViewHolder(View itemView) {
            super(itemView);
            addr_name = (TextView) itemView.findViewById(R.id.addr_name);
            addr_info = (TextView) itemView.findViewById(R.id.addr_info);
            position_ll = (LinearLayout) itemView.findViewById(R.id.position_ll);


        }
    }
    public interface PositionLLCallBack {
        void  positionOnClick(String addrInfo);
    }
    public void setPositionOnClick(PositionLLCallBack callBack){
        this.callBack = callBack;
    }
}
