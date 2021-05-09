package com.ihxjie.monday.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xjie
 * @since 2021-04-26
 */
public class PositionInfo implements Serializable {

    private Integer positionId;

    private Long attendanceId;;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 精度
     */
    private String accuracy;

    /**
     * 提供者
     */
    private String provider;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域
     */
    private String district;

    /**
     * 具体地址
     */
    private String address;

    /**
     * 兴趣点
     */
    private String poi;

    /**
     * 定位类型
     */
    private String locationType;

    /**
     * GPS星数
     */
    private String gpsSatellites;

    /**
     * 距离
     */
    private String distance;

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getGpsSatellites() {
        return gpsSatellites;
    }

    public void setGpsSatellites(String gpsSatellites) {
        this.gpsSatellites = gpsSatellites;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "PositionInfo{" +
                "positionId=" + positionId +
                ", attendanceId=" + attendanceId +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", provider='" + provider + '\'' +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", poi='" + poi + '\'' +
                ", locationType='" + locationType + '\'' +
                ", gpsSatellites='" + gpsSatellites + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }
}
