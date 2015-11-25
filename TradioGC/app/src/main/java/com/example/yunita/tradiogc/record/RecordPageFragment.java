// tab layout taken from https://guides.codepath.com/android/google-play-style-tabs-using-tablayout
// (C) 2015 CodePath modified by Cloud 9

package com.example.yunita.tradiogc.record;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yunita.tradiogc.R;
import com.example.yunita.tradiogc.inventory.Inventory;
import com.example.yunita.tradiogc.inventory.Item;
import com.example.yunita.tradiogc.login.LoginActivity;
import com.example.yunita.tradiogc.trade.Trade;
import com.example.yunita.tradiogc.trade.Trades;

public class RecordPageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private ListView listView;
    private ArrayAdapter<Trade> currentTradesArrayAdapter;
    private ArrayAdapter<Trade> pastTradesArrayAdapter;
    private ArrayAdapter<Trade> completedTradesArrayAdapter;
    private Trades temp = LoginActivity.USERLOGIN.getTrades() ;

    public static RecordPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RecordPageFragment fragment = new RecordPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        listView = (ListView) view;

        switch (mPage){
            case 1:// current = pending, offered, accepted
                setCurrentTradesView();
                break;
            case 2: // completed
                setPastTradesView();
                break;
            case 3: // past = accepted, declined
                break;
        }
        return view;
    }

    /**
     * Sets the current trades view.
     */
    public void setCurrentTradesView(){
        Trades currentTrades = new Trades(temp);
        currentTrades = currentTrades.getCurrentTrades();
        currentTradesArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.trades_list_item, currentTrades);
        listView.setAdapter(currentTradesArrayAdapter);
    }

    /**
     * Sets the past trades view.
     */
    public void setPastTradesView(){
        Trades pastTrades = new Trades(temp);
        pastTrades = pastTrades.getPastTrades();
        pastTradesArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.trades_list_item, pastTrades);
        listView.setAdapter(pastTradesArrayAdapter);
    }

    /**
     * Sets the completed trades view.
     */
    public void setCompletedTradesView(){
        Trades completedTrades = new Trades(temp);
        completedTrades = completedTrades.getPastTrades();
        completedTradesArrayAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.trades_list_item, completedTrades);
        listView.setAdapter(completedTradesArrayAdapter);
    }

}