package com.tying.tweather.domain;

import org.litepal.annotation.Column;
import org.litepal.exceptions.DataSupportException;

public class Province extends DataSupportException {

    public Province(String errorMessage) {
        super(errorMessage);
    }

    @Column(unique = true, nullable = false)
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
