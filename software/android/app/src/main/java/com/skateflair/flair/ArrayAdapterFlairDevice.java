package com.skateflair.flair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by myron on 2/10/16.
 */
public class ArrayAdapterFlairDevice extends ArrayAdapter<DatumFlairDevice> {

    public class ItemViewModel {
        TextView tvName;
        TextView tvAddress;
        CheckBox chkSelected;
    }

    public ArrayAdapterFlairDevice(Context context, int resource, List<DatumFlairDevice> deviceList) {
        super(context, resource, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ItemViewModel item_model = null;

        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater itemInflator = LayoutInflater.from(getContext());

            itemView = itemInflator.inflate(R.layout.view_flair_device, null);

            item_model = new ItemViewModel();
            item_model.tvName = (TextView)itemView.findViewById(R.id.txtDeviceName);
            item_model.tvAddress = (TextView)itemView.findViewById(R.id.txtDeviceAddress);
            item_model.chkSelected = (CheckBox)itemView.findViewById(R.id.chkDeviceSelected);
            itemView.setTag(item_model);
        }
        else {
            item_model = (ItemViewModel)itemView.getTag();
        }

        DatumFlairDevice item = getItem(position);

        if (item != null)
        {
            String name = item.getName();
            String address = item.getAddress();
            Boolean selected = item.getSelected();

            item_model.tvName.setText(name);
            item_model.tvAddress.setText(address);
            item_model.chkSelected.setChecked(selected);
        }

        return itemView;
    }
}
