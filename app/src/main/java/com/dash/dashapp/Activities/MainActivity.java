package com.dash.dashapp.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.dash.dashapp.Fragments.NewsFragment;
import com.dash.dashapp.Fragments.PortfolioFragment;
import com.dash.dashapp.Fragments.PriceFragment;
import com.dash.dashapp.Fragments.ProposalsFragment;
import com.dash.dashapp.R;
import com.dash.dashapp.Utils.SharedPreferencesManager;
import com.dash.dashapp.Utils.URLs;

import java.util.Locale;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements
        PortfolioFragment.OnFragmentInteractionListener,
        PriceFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private NewsFragment newsFragment;
    private ProposalsFragment proposalFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();
                    return true;
                case R.id.navigation_proposals:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, proposalFragment).commit();
                    return true;
                case R.id.navigation_prices:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, PriceFragment.newInstance()).commit();
                    return true;
                case R.id.navigation_portfolio:
                    getSupportFragmentManager().beginTransaction().replace(R.id.content, PortfolioFragment.newInstance()).commit();
                    return true;
            }
            return false;
        }

    };


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        newsFragment = NewsFragment.newInstance();
        proposalFragment = ProposalsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content, newsFragment).commit();

        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
