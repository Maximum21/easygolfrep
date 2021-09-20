package com.minhhop.easygolf.framework.models;

import android.graphics.Color;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.minhhop.easygolf.R;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Tee extends RealmObject {

    public static final String TEE_DEFAULT = "BLUE";

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("idClub")
    @Expose
    private String idClub;

    @SerializedName("last_updated")
    @Expose
    private String lastUpdated;

    @SerializedName("date_created")
    @Expose
    private String dateCreated;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("type_id")
    @Expose
    private String typeId;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("distance")
    @Expose
    private double distance;

    @SerializedName("yard")
    @Expose
    private int yard;

    @SerializedName("par")
    @Expose
    private int par;

    @SerializedName("cr")
    @Expose
    private Double cr;

    @SerializedName("sr")
    @Expose
    private Double sr;

    public void setType(String type) {
        this.type = type;
    }

    public int getPar() {
        return par;
    }

    public int getId() {
        return id;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getType() {
        return type;
    }

    public String getTypeId() {
        return typeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public String getIdClub() {
        return idClub;
    }

    public void setIdClub(String idClub) {
        this.idClub = idClub;
    }

    /**
     *
     * @return
     */
    public Double getCr() {
        return cr;
    }

    /**
     * Slope Rating
     * @return Double
     */
    public Double getSr() {
        return sr;
    }


    public int getResIcon() {
        if (!TextUtils.isEmpty(this.type)) {
            int resIcon;
            String compareTarget = this.type.toUpperCase();
            switch (compareTarget) {
                case "RED":
                    resIcon = R.drawable.ic_red_tee_icon;
                    break;
                case "WHITE":
                    resIcon = R.drawable.ic_white_tee_icon;
                    break;
                case "GREEN":
                    resIcon = R.drawable.ic_tee_green_icon;
                    break;
                case "BLUE":
                    resIcon = R.drawable.ic_icon_blue_tee;
                    break;
                case "YELLOW":
                    resIcon = R.drawable.ic_yelow_tee_icon;
                    break;
                case "BLACK":
                    resIcon = R.drawable.ic_icon_black_tee;
                    break;
                case "PLATINUM":
                    resIcon = R.drawable.ic_icon_platinum_tee;
                    break;

                case "PURPLE":
                    resIcon = R.drawable.ic_icon_purple_tee;
                    break;

                case "SEPIA":
                    resIcon = R.drawable.ic_icon_sepia_tee;
                    break;

                case "GREY":
                    resIcon = R.drawable.ic_icon_grey_tee;
                    break;

                case "GRAY":
                    resIcon = R.drawable.ic_icon_gray_tee;
                    break;

                case "SHARK":
                    resIcon = R.drawable.ic_icon_shark_tee;
                    break;

                case "BROWN":
                    resIcon = R.drawable.ic_icon_brown_tee;
                    break;

                case "LIME":
                    resIcon = R.drawable.ic_icon_lime_tee;
                    break;

                case "PINK":
                    resIcon = R.drawable.ic_icon_pink_tee_icon;
                    break;

                case "ORANGE":
                    resIcon = R.drawable.ic_orange_tee_icon;
                    break;

                case "JADE":
                    resIcon = R.drawable.ic_icon_jade_tee;
                    break;

                case "COPPER":
                    resIcon = R.drawable.ic_icon_copper_tee;
                    break;

                case "SLIVER":
                    resIcon = R.drawable.ic_icon_silver_tee;
                    break;
                case "GOLD":
                    resIcon = R.drawable.ic_icon_tee_gold;
                    break;
                default:
                    resIcon = R.drawable.ic_orange_tee_icon;
            }

            return resIcon;
        }
        return 0;

    }


    public int getResOptionIcon() {

        if (!TextUtils.isEmpty(this.type)) {
            int resIcon;
            String compareTarget = this.type.toUpperCase();
            switch (compareTarget) {
                case "RED":
                    resIcon = Color.parseColor("#c6342d");
                    break;
                case "WHITE":
                    resIcon = Color.parseColor("#FFF1F1F1");
                    break;
                case "GREEN":
                    resIcon = Color.parseColor("#4c8e06");
                    break;
                case "BLUE":
                    resIcon = Color.parseColor("#FF0D5C93");
                    break;
                case "YELLOW":
                    resIcon = Color.parseColor("#f6dc03");
                    break;
                case "BLACK":
                    resIcon = Color.parseColor("#000000");
                    break;
                case "PLATINUM":
                    resIcon = Color.parseColor("#d4d4d4");
                    break;

                case "PURPLE":
                    resIcon = Color.parseColor("#dea0dd");
                    break;

                case "SEPIA":
                    resIcon = Color.parseColor("#704214");
                    break;

                case "GREY":
                    resIcon = Color.parseColor("#666666");
                    break;

                case "GRAY":
                    resIcon = Color.parseColor("#a9a9a9");
                    break;

                case "SHARK":
                    resIcon = Color.parseColor("#006272");
                    break;

                case "BROWN":
                    resIcon = Color.parseColor("#793802");
                    break;

                case "LIME":
                    resIcon = Color.parseColor("#c8e260");
                    break;

                case "PINK":
                    resIcon = Color.parseColor("#cf5b85");
                    break;

                case "ORANGE":
                    resIcon = Color.parseColor("#e8722e");
                    break;

                case "JADE":
                    resIcon = Color.parseColor("#00a86b");
                    break;

                case "COPPER":
                    resIcon = Color.parseColor("#cf8d6d");
                    break;

                case "SLIVER":
                    resIcon = Color.parseColor("#bbc2c2");
                    break;
                case "GOLD":
                    resIcon = Color.parseColor("#F5DA31");
                    break;
                default:
                    resIcon = Color.parseColor("#FF0D5C93");
            }
            return resIcon;
        }
        return 0;
    }

    //
    private int getIndex() {
        if (!TextUtils.isEmpty(this.type)) {
            int index = 0;
            switch (this.type) {
                case "GREEN":
                    index = 0;
                    break;
                case "YELLOW":
                    index = 1;
                    break;
                case "BLUE":
                    index = 2;
                    break;
                case "WHITE":
                    index = 3;
                    break;
                case "RED":
                    index = 4;
                    break;

            }

            return index;
        }
        return 0;
    }

    private static int partition(List<Tee> arr, int low, int high) {
        int pivot = arr.get(high).getIndex();
        int i = (low - 1);
        for (int j = low; j < high; j++) {

            if (arr.get(j).getIndex() <= pivot) {
                i++;

                // swap arr[i] and arr[j]
                Tee temp = arr.get(i);
                arr.set(i, arr.get(j));
                arr.set(j, temp);
            }
        }

        Tee temp = arr.get(i + 1);
        arr.set(i + 1, arr.get(high));
        arr.set(high, temp);


        return i + 1;
    }


    public static void quickSort(List<Tee> arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }


    public int getYard() {
        return yard;
    }

    public static Tee getDefault(List<Tee> tees) {
        return getTeeByType(tees, "BLUE");
    }


    public static Tee getTeeByType(List<Tee> tees, String type) {
        if (TextUtils.isEmpty(type)) {
            type = "BLUE";
        }
        if (tees == null) {
            return null;
        }
        if (tees.size() <= 0) {
            return null;
        }
        Tee target = tees.get(0);
        for (int i = 1; i < tees.size(); i++) {
            if (tees.get(i).getType().equalsIgnoreCase(type)) {
                target = tees.get(i);
                break;
            }
        }
        return target;
    }


    public static int getPositionByType(List<Tee> tees, String type) {
        if (TextUtils.isEmpty(type)) {
            type = "BLUE";
        }
        if (tees == null) {
            return 0;
        }
        if (tees.size() <= 0) {
            return 0;
        }
        int pos = 0;
        for (int i = 0; i < tees.size(); i++) {
            if (tees.get(i).getType().equalsIgnoreCase(type)) {
                pos = i;
                break;
            }
        }

        return pos;
    }


}
