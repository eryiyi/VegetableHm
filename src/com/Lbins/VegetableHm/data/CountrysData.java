package com.Lbins.VegetableHm.data;


import com.Lbins.VegetableHm.module.CountryObj;

import java.util.List;

/**
 * Created by Administrator on 2016/2/10.
 */
public class CountrysData extends Data {
    private List<CountryObj> data;

    public List<CountryObj> getData() {
        return data;
    }

    public void setData(List<CountryObj> data) {
        this.data = data;
    }
}
