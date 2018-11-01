package weber.kaden.myapplication.ui;

import android.app.ActionBar;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weber.kaden.common.model.Model;
import weber.kaden.common.model.DestinationCard;
import weber.kaden.common.model.Game;
import weber.kaden.common.model.Model;
import weber.kaden.common.model.TrainCard;
import weber.kaden.common.model.DestinationCard;
import weber.kaden.common.model.Game;

import weber.kaden.common.model.TrainCardType;
import weber.kaden.myapplication.R;
import weber.kaden.myapplication.model.ClientFacade;
import weber.kaden.myapplication.ui.map.DisplayRoute;
import weber.kaden.myapplication.ui.map.DisplayRoutes;
import weber.kaden.myapplication.ui.map.Location;
import weber.kaden.myapplication.ui.map.Locations;
import weber.kaden.myapplication.ui.turnmenu.ChooseDestinationCardsFragment;
import weber.kaden.myapplication.ui.turnmenu.ChooseDestinationCardsPresenter;
import weber.kaden.myapplication.ui.turnmenu.ChooseTrainCardsFragment;
import weber.kaden.myapplication.ui.turnmenu.ChooseTrainCardsPresenter;
import weber.kaden.myapplication.ui.turnmenu.SeeOtherPlayersFragment;

public class GameActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GameViewInterface,
        ClaimRoute.OnFragmentInteractionListener {
    GameActivity instance = this;
    GameAdapter adapter;
    TrainCardAdapter trainCardAdapter;
    PointsAdapter pointsAdapter;
    List<DestinationCard> destCards = new ArrayList<>();
    List<TrainCard> trainCards = new ArrayList<>();
    List<Integer> points = new ArrayList<>();
    //map Constants
    private static final float DEFAULT_ZOOM = (float) 4.0;
    private static final float MIN_ZOOM = (float) 4.0;
    private static final float MAX_ZOOM = (float) 5.5;
    private static final double DEFAULT_VIEW_LAT = 40;
    private static final double DEFAULT_VIEW_LONG = -98;
    private static final float DEFAULT_VIEW_BEARING = 9;
    private static final double MAX_NORTH = 46;
    private static final double MAX_EAST = -83;
    private static final double MAX_SOUTH = 34;
    private static final double MAX_WEST = -115;

    private static final float ROUTE_WIDTH = 22;
    private static final double SECOND_ROUTE_OFFSET = 0.6;

    private Locations mLocations;
    private Map mRouteMap;
    List<PatternItem> unclaimedRoutePattern;
    private List<PatternItem> claimedRoutePattern;
    private GamePresenter mPresenter = new GamePresenter(this, new ClientFacade());
    private DrawerLayout mDrawerLayout;
    private boolean mClaimingRouteFlag;

    //TEMP FOR PHASE 2
    Button mTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.myInfoRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameAdapter(this, destCards);
        recyclerView.setAdapter(adapter);
        // set up the RecyclerView
        RecyclerView recyclerViewTrains = findViewById(R.id.myInfoRecycleTrains);
        recyclerViewTrains.setLayoutManager(new LinearLayoutManager(this));
        trainCardAdapter = new TrainCardAdapter(this, trainCards);
        recyclerViewTrains.setAdapter(trainCardAdapter);
        // set up the RecyclerView
        RecyclerView recyclerPoints = findViewById(R.id.myInfoRecyclePoints);
        recyclerPoints.setLayoutManager(new LinearLayoutManager(this));
        pointsAdapter = new PointsAdapter(this, points);
        recyclerPoints.setAdapter(pointsAdapter);
        // end RecyclerView
        // Hide both the navigation bar and the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //add map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocations = new Locations();

        //Nav Drawer
        mDrawerLayout = findViewById(R.id.activity_game_layout);

        NavigationView navigationView = findViewById(R.id.turn_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.turn_menu_destination_cards:
                        DestinationCardsTask destinationCardsTask = new DestinationCardsTask();
                        destinationCardsTask.execute();
                        break;
                    case R.id.turn_menu_train_cards:
                        ChooseTrainCardsFragment chooseTrainCardsFragment = new ChooseTrainCardsFragment();
                        ChooseTrainCardsPresenter trainCardsPresenter = new ChooseTrainCardsPresenter(chooseTrainCardsFragment, new ClientFacade());

                        Bundle chooseTrainCardArgs = new Bundle();
                        chooseTrainCardArgs.putSerializable("faceUpCards", (Serializable) trainCardsPresenter.getFaceUpTrainCards());
                        chooseTrainCardsFragment.setArguments(chooseTrainCardArgs);

                        chooseTrainCardsFragment.show(getSupportFragmentManager(), "ChooseTrainCardsFragment");
                        break;
                    case R.id.turn_menu_claim_route:
                        //Claim route is different since we're not opening up a dialog, we're using the map
                        mClaimingRouteFlag = true;
                        break;
                    case R.id.turn_menu_see_other_players:
                        DialogFragment seeOtherPlayersFragment = new SeeOtherPlayersFragment();
                        seeOtherPlayersFragment.show(getSupportFragmentManager(), "ChooseTrainCardsFragment");
                        break;
                    case R.id.turn_menu_chat:
                        DialogFragment chatFragment = new ChatFragment();
                        ((ChatFragment) chatFragment).setMessages(Model.getInstance().getCurrentGame().getChat());
                        chatFragment.show(getSupportFragmentManager(), "ChatFragment");
                }

                return true;
            }
        });
        //make unclaimed route pattern
        unclaimedRoutePattern = Arrays.<PatternItem>asList(
                new Gap(10), new Dash(70) );
        //make claimed route pattern
        claimedRoutePattern = Arrays.<PatternItem>asList(
                 new Dash(100) );

        mClaimingRouteFlag = false;

        //init route list
        mRouteMap = new HashMap();

        //init test button
        mTestButton = findViewById(R.id.tempTestButton);
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.runPhase2Test();
            }
        });
    }
    public void setMyNewValues(List<DestinationCard> nDestCards, List<TrainCard> nTrainCards, List<Integer> nPoints){
        destCards.clear();
        destCards.addAll(nDestCards);
        trainCards.clear();
        trainCards.addAll(nTrainCards);
        trainCardAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        points.clear();
        points.addAll(nPoints);
        pointsAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set map style
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        //set min and max zoom
        googleMap.setMinZoomPreference(MIN_ZOOM);
        googleMap.setMaxZoomPreference(MAX_ZOOM);
        // Move to initial view
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(DEFAULT_VIEW_LAT, DEFAULT_VIEW_LONG), DEFAULT_ZOOM, 0, DEFAULT_VIEW_BEARING)));
        //constrain map to game area
        LatLngBounds bounds = new LatLngBounds(new LatLng(MAX_SOUTH, MAX_WEST), new LatLng(MAX_NORTH, MAX_EAST));
        googleMap.setLatLngBoundsForCameraTarget(bounds);
        //add city markers
        for (Location location : mLocations.getLocations()) {
            googleMap.addMarker(new MarkerOptions().position(location.getCoords())
                    .title(location.getCity())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.steamtrain_dark)));
        }


        //add routes TODO: make double routes look real good, refactor to route creation method
        DisplayRoutes routes = new DisplayRoutes(mLocations);
        for ( DisplayRoute route : routes.getRoutes()){
            String routeColor = route.getColor();
            //check for double route
            if(routeColor.contains(",")){
                createDoubleRoute(googleMap, route);
                route.setColor(getFirstColor(routeColor));
            }
            //add regular route
            addRoute(googleMap, route, route.getCity1().getCoords(), route.getCity2().getCoords());
        }

        //make routes clickable
        googleMap.setOnPolylineClickListener(this);

    }
    private void addRoute(GoogleMap googleMap, DisplayRoute route, LatLng loc1, LatLng loc2){
        Polyline line = googleMap.addPolyline(new PolylineOptions().clickable(true)
                .add(loc1, loc2)
                .width(ROUTE_WIDTH)
                .geodesic(true)
                .pattern(unclaimedRoutePattern)
                .color(getRouteColor(route.getColor())));
        line.setTag(route.getLength());
        mRouteMap.put(line, route);
    }

    private String getFirstColor(String routeColor) {
        return routeColor.substring(0, routeColor.indexOf(","));
    }

    private void createDoubleRoute(GoogleMap googleMap, DisplayRoute route) {
        route.setColor(getSecondColor(route.getColor()));
        double slope = slope(route.getCity1(), route.getCity2());
        LatLng newCity1 = offsetCoords(route.getCity1().getCoords(), slope);
        LatLng newCity2 = offsetCoords(route.getCity2().getCoords(), slope);
        addRoute(googleMap, route, newCity1, newCity2);

    }

    private LatLng offsetCoords(LatLng input, double slope){
        double perpendicularSlope = -1/slope;
        double theta = Math.atan(perpendicularSlope);
        double deltaX = SECOND_ROUTE_OFFSET * Math.cos(theta);
        double deltaY = SECOND_ROUTE_OFFSET * Math.sin(theta);
        double newX = input.longitude + deltaX;
        double newY = input.latitude + deltaY;
        return new LatLng(newY, newX);
    }
    private double slope(Location location1, Location location2){
        return (location2.getCoords().latitude - location1.getCoords().latitude)
               / (location2.getCoords().longitude - location1.getCoords().longitude);
    }
    private String getSecondColor(String routeColor) {
        return routeColor.substring(routeColor.indexOf(" ") + 1);
    }

    private int getRouteColor(String color) {
        switch (color){
            case "White":
                return getResources().getColor(R.color.Passenger);
            case "Black":
                return getResources().getColor(R.color.Hopper);
            case "Blue":
                return getResources().getColor(R.color.Tanker);
            case "Green":
                return getResources().getColor(R.color.Caboose);
            case "Red":
                return getResources().getColor(R.color.Coal);
            case "Purple":
                return getResources().getColor(R.color.Box);
            case "Orange":
                return getResources().getColor(R.color.Freight);
            case "Yellow":
                return getResources().getColor(R.color.Reefer);
            case "Gray":
                return getResources().getColor(R.color.Gray);
        }
        return R.color.Gray;
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        //check if the route can be claimed
        //otherwise display route length
        if(mClaimingRouteFlag && mPresenter.routeClaimable(
                (int)polyline.getTag(), getRouteType(polyline))){ // and route can be claimed...
            ClaimRoute.newInstance(getCity1(polyline), getCity2(polyline));
        }
        Toast.makeText(this, "Cost: " + String.valueOf(polyline.getTag()), Toast.LENGTH_SHORT).show();
    }
    private TrainCardType getRouteType(Polyline polyline){
        DisplayRoute route = (DisplayRoute) mRouteMap.get(polyline);
        String color = route.getColor();
        switch (color){
            case "White":
                return TrainCardType.PASSENGER;
            case "Black":
                return TrainCardType.HOPPER;
            case "Blue":
                return TrainCardType.TANKER;
            case "Green":
                return TrainCardType.CABOOSE;
            case "Red":
                return TrainCardType.COAL;
            case "Purple":
                return TrainCardType.BOX;
            case "Orange":
                return TrainCardType.FREIGHT;
            case "Yellow":
                return TrainCardType.REEFER;
            case "Gray": //??
                return TrainCardType.LOCOMOTIVE;
                default:
                    return TrainCardType.LOCOMOTIVE;
        }
    }
    private String getCity1(Polyline polyline){
        DisplayRoute route = (DisplayRoute) mRouteMap.get(polyline);
        return route.getCity1().getCity();
    }
    private String getCity2(Polyline polyline){
        DisplayRoute route = (DisplayRoute) mRouteMap.get(polyline);
        return route.getCity2().getCity();
    }

    public void sendMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public class DestinationCardsTask extends AsyncTask<Void, Void, Boolean> {
        private String errorString = "";
        private List<DestinationCard> destinationCards;
        private ChooseDestinationCardsFragment fragment = new ChooseDestinationCardsFragment();

        DestinationCardsTask() {
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            ClientFacade clientFacade = new ClientFacade();
            ChooseDestinationCardsPresenter gameListPresenter = new ChooseDestinationCardsPresenter(fragment, clientFacade);
            try {
                destinationCards = gameListPresenter.getDrawableDestinationCards();
            } catch (Exception e) {
                errorString = e.getMessage();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                Bundle chooseDestinationCardsArgs = new Bundle();
                chooseDestinationCardsArgs.putSerializable("cards", (Serializable) destinationCards);
                fragment.setArguments(chooseDestinationCardsArgs);
                fragment.show(getSupportFragmentManager(), "ChooseDestinationCardsFragment");
            } else {
                Toast.makeText(instance, errorString, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }


}
