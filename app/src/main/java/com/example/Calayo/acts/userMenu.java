package com.example.Calayo.acts;

import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import androidx.fragment.app.DialogFragment;

public class userMenu extends DialogFragment {
     @Override
    public void onStart(){
        super.onStart();
        if(getDialog() == null&& getDialog().getWindow() == null){
            dismiss();
        }else {
            Window window = getDialog().getWindow();
            window.setLayout(600,ViewGroup.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.START);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }
    }
//    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        TextView dashB;
//
//        TextView shop;
//
//        TextView user_profile;
//        TextView booking;
//
//        dashB.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), ManagerDashB.class);
//            startActivity(intent);
//            dismiss();
//        });
//        shop.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), ManagerHome.class);
//            startActivity(intent);
//            dismiss();
//        });
//        user_profile.setOnClickListener(v -> {
//            Intent intent = new Intent(getContext(), userDashB_setting.class);
//            startActivity(intent);
//            dismiss();
//        });
//        booking.setOnClickListener(v -> {
//            dismiss();
//        });

//        return view;
//    }

}
