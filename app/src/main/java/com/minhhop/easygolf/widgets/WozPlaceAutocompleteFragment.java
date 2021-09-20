package com.minhhop.easygolf.widgets;

import android.content.Intent;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

public class WozPlaceAutocompleteFragment extends PlaceAutocompleteFragment {

    @Override
    public void onActivityResult(int var1, int var2, Intent var3) {
        super.onActivityResult(var1, var2, var3);
        if (var1 == 30421) {
            if (var2 == -1) {
                Place var4 = PlaceAutocomplete.getPlace(this.getActivity(), var3);
                if(var4.getAddress() != null)
                this.setText(var4.getAddress().toString());

            }
        }

    }
}
