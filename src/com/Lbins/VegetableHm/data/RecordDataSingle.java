package com.Lbins.VegetableHm.data;


import com.Lbins.VegetableHm.dao.RecordMsg;

/**
 * Created by Administrator on 2016/2/18.
 */
public class RecordDataSingle extends Data {
    private RecordMsg data;

    public RecordMsg getData() {
        return data;
    }

    public void setData(RecordMsg data) {
        this.data = data;
    }
}
