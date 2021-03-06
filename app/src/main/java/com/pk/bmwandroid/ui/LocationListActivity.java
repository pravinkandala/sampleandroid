package com.pk.bmwandroid.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pk.bmwandroid.R;
import com.pk.bmwandroid.data.repository.LocationRepository;
import com.pk.bmwandroid.model.Location;
import com.pk.bmwandroid.model.factory.LocationComparatorFactory.SortingCriteria;
import com.pk.bmwandroid.network.LocalSearchManager;
import com.pk.bmwandroid.network.ServerCallback;
import com.pk.bmwandroid.ui.adapter.LocationAdapter;

import java.util.List;

import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


public class LocationListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.Adapter mAdapter;

    Context mContext;
    private LocationRepository mLocationRepository;
    private SortingCriteria mSortingCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;

        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main);


        //Initializing Repository
        this.mLocationRepository = new LocationRepository();
        this.mSortingCriteria = SortingCriteria.NAME;


        // Get Initial Data - on Notified - fillCards
        initList();

        // Swipe to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });


    }

    /**
     * Load json from url
     */
    public void initList() {
        mSwipeRefreshLayout.setRefreshing(true);
        LocalSearchManager.getLocalSearchResults(mContext, getString(R.string.json_url), new ServerCallback() {
            @Override
            public void onSuccess(final List<Location> locations) {
                mLocationRepository.addAll(locations);
                fillCards(mLocationRepository.getAll(mSortingCriteria, mContext));
            }
        });
    }

    /**
     * Fill data into cards
     */
    public void fillCards(final List<Location> locations) {

        mAdapter = new LocationAdapter(this, locations);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //stop refresh progress
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                initList();
                return true;

            case R.id.menu_sort_by_name:
                fillCards(this.mLocationRepository.getAll(SortingCriteria.NAME, mContext));
                return true;

            case R.id.menu_sort_by_time:
                fillCards(this.mLocationRepository.getAll(SortingCriteria.TIME, mContext));

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}