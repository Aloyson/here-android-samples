package com.here.android.example.routing;

import android.app.Activity;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteElement;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteTta;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.RED;

/**
 * Created by aloyson_decosta on 22-08-2017.
 */

public class Routing {

    private Activity m_activity;
    private MapRoute m_mapRoute;
    private MapRoute m_mapRoute2;
    private  Navigation navigation;
    private Map map;
    private Button startNavigation;
    private TextView textView;

    public Routing(Activity activity) {
        m_activity = activity;
        navigation=new Navigation(m_activity);
    }



    public void initCreateRouteButton(final GeoCoordinate geoCoordinate, final GeoCoordinate geoCoordinate2, final Map m_map) {
                if (m_map != null && m_mapRoute != null && m_mapRoute2!=null) {
                    m_map.removeMapObject(m_mapRoute);
                    m_map.removeMapObject(m_mapRoute2);
                    m_mapRoute = null;
                    m_mapRoute2 = null;
                }
                    /*
                     * The route calculation requires local map data.Unless there is pre-downloaded
                     * map data on device by utilizing MapLoader APIs, it's not recommended to
                     * trigger the route calculation immediately after the MapEngine is
                     * initialized.The INSUFFICIENT_MAP_DATA error code may be returned by
                     * CoreRouter in this case.
                     *
                     */
                    map=m_map;
        createRoute(geoCoordinate,geoCoordinate2,m_map);
//        downloadCatalogAndSkin();

    }

    /* Creates a route from 4350 Still Creek Dr to Langley BC with highways disallowed */
        private void createRoute(GeoCoordinate geoCoordinate, GeoCoordinate geoCoordinate2, final Map m_map) {
        /* Initialize a CoreRouter */
            CoreRouter coreRouter = new CoreRouter();


        /* Initialize a RoutePlan */
            RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
            RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Enable highway in this route. */
            //routeOptions.setHighwaysAllowed(true);
        /* Calculate the shortest route available. */
            routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
          //  routeOptions.setTollRoadsAllowed(false);

        /* Calculate 1 route. */
            routeOptions.setRouteCount(2);
        /* Finally set the route option */
            routePlan.setRouteOptions(routeOptions);

            RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(0, 0));
            RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(0, 0));
        /* Define waypoints for the route */
//        /* START: 4350 Still Creek Dr*/
            // RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(15.283549, 73.982564));
//        /* END: Langley BC */
            //RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(15.486710, 73.822039));


            //    try{
            //  Util utilobj=new Util(context);

            //if(spinner.getSelectedItem().toString().equals("Margao")){
//            String start=utilobj.getProperty(spinner.getSelectedItem().toString());
//            String[] tokens=start.split(",");
//            startPoint = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokens[0]),Float.parseFloat(tokens[1])));
//            //}
//
//
//            String end=utilobj.getProperty(spinner2.getSelectedItem().toString());
//            String[] tokend=end.split(",");
//            destination = new RouteWaypoint(new GeoCoordinate(Float.parseFloat(tokend[0]),Float.parseFloat(tokend[1])));
            if (geoCoordinate == null && geoCoordinate2 == null) {
                Toast.makeText(m_activity,
                        "Select Source and Destination",
                        Toast.LENGTH_LONG).show();
                return;
            } else {

                startPoint = new RouteWaypoint(new GeoCoordinate(geoCoordinate));
                destination = new RouteWaypoint(new GeoCoordinate(geoCoordinate2));

            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        /* Add both waypoints to the route plan */
            routePlan.addWaypoint(startPoint);
            routePlan.addWaypoint(destination);

        /* Trigger the route calculation,results will be called back via the listener */
            coreRouter.calculateRoute(routePlan,
                    new Router.Listener<List<RouteResult>, RoutingError>() {
                        @Override
                        public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                        }

                        @Override
                        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                             RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                            if (routingError == RoutingError.NONE) {
                                if (routeResults.get(0).getRoute() != null) {
                                /* Create a MapRoute so that it can be placed on the map */
                                    m_mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                    m_mapRoute.setManeuverNumberVisible(true);




                                /* Create a MapRoute2 so that it can be placed on the map */
                                            m_mapRoute2 = new MapRoute(routeResults.get(1).getRoute());

                                /* Show the maneuver number on top of the route */
                                            m_mapRoute2.setManeuverNumberVisible(true);
                                             m_mapRoute2.setColor(GRAY);




                                /* Add the MapRoute to the map */
                                    m_map.addMapObject(m_mapRoute2);
                                    m_map.addMapObject(m_mapRoute);


                                    GeoBoundingBox gbb = routeResults.get(0).getRoute()
                                            .getBoundingBox();
                                    m_map.zoomTo(gbb, Map.Animation.NONE,
                                            Map.MOVE_PRESERVE_ORIENTATION);

                                    Route route=m_mapRoute.getRoute();

//                                    List<RouteElement> re=route.getRouteElements().getElements();
//                                    int i=0;
//                                    RouteElement rel;

//                                   while(i<re.size()) {
//                                        rel = re.get(i);
//                                    String x=rel.getRoadElement().getRoadName();
//                                   // String x=rel.getRoadElement().getAttributes().toString();
////                                     Boolean b= rel.getRoadElement().isPedestrian();//checks if only pedistrain
//                                      // Toast.makeText(m_activity.getApplicationContext(),"road Ele: "+x+"type: "+rel.getType(),Toast.LENGTH_LONG).show();
//                                       i++;
//                                   }
                                    //show distance in meters
                                   float dist= route.getLength();
                                  //for Tta(Time to arrival
                                    RouteTta rrta=route.getTta(Route.TrafficPenaltyMode.OPTIMAL,Route.WHOLE_ROUTE);
                                    int getTimesec=rrta.getDuration();

                                   //Toast.makeText(m_activity.getApplicationContext(),"Distance is:"+rrta.getDuration(),Toast.LENGTH_SHORT).show();
                                   List<GeoCoordinate> allCordinates= m_mapRoute.getRoute().getRouteGeometry();
                                    AddTextBox(allCordinates.get(allCordinates.size()/2),dist,getTimesec);
                                    //navigation.startNavigationTrack(m_map,m_mapRoute);
                                    startNavigationTrack();

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
//
                                } else {
                                    Toast.makeText(m_activity,
                                            "Error:route results returned is not valid",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route calculation returned error code: " + routingError,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

public void startNavigationTrack(){
            startNavigation = (Button)m_activity.findViewById(R.id.button);

            startNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigation.startGuidance(map,m_mapRoute);
                }
            });
    }


    //for TextBox to display Distance
    private void AddTextBox(GeoCoordinate geo,float dist,int getTimesec){
                //Create the svg mark-up
//        Image image = new Image();
//        try {
//            image.setImageResource(R.mipmap.pin);
//        } catch (final IOException e) {
//            e.printStackTrace();
//        }
       // MapMarker mMarker = new MapMarker(geo, image);
        MapLabeledMarker labeledMarker=new MapLabeledMarker(geo);
       // labeledMarker.setCoordinate(geo);
        labeledMarker.setLabelText("eng","hello");
        map.addMapObject(labeledMarker);
     textView=(TextView)m_activity.findViewById(R.id.textView);


        //cal dist
        //dist=dist/1000;//covert in km
       float distance=Math.round(dist/10);
        distance/=100;
        //cal tym
        int hours=getTimesec/3600;
        int min=(getTimesec-hours*3600)/60;
        String Time="";

        if(hours==0)
           Time=min+" mins";
        else if(min==0)
            Time=getTimesec+" secs";
        else
           Time=hours+" hrs "+min+" mins";

        textView.setText("dist: "+distance+" km\n"+"Time:"+Time);
        textView.setVisibility(View.VISIBLE);

    }

}
