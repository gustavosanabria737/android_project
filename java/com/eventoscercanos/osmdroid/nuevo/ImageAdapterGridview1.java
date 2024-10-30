package com.eventoscercanos.osmdroid.nuevo;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

public class ImageAdapterGridview1 extends BaseAdapter {
    private final Context mContext;

    public ImageAdapterGridview1(Context c) {
        mContext = c;
    }

    public int getCount() {
        return negocios.length;
    }

    public Object getItem(int position) {
        return negocios[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(new GridView.LayoutParams(54, 63));//todo try dimensions  in diferents devices
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        try {
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(negocios[position] + ".png")));

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return imageView;
    }

    
    private String[] negocios = {

            "mk_restaurant",
            "mk_rest_fooddeliveryservice",
            "mk_rest_foodtruck",
            "mk_rest_romantic",
            "mk_rest_terrace",
            "mk_rest_sandwich",
            "mk_rest_burger",
            "mk_rest_pizzaria",
            "mk_rest_hotdog",
            "mk_rest_thai",
            "mk_rest_turkish",
            "mk_rest_fastfood",
            "mk_rest_breakfast",
            "mk_rest_milk_and_cookies",
            "mk_rest_muffin_bagle",
            "mk_rest_bread",
            "mk_rest_italian",
            "mk_rest_japanese_food",
            "mk_rest_chinese",
            "mk_rest_japanese_sweet",
            "mk_rest_korean",
            "mk_rest_buffet",
            "mk_rest_barbecue",
            "mk_rest_gourmet",
            "mk_rest_butcher",
            "mk_rest_greek",
            "mk_rest_steakhouse",
            "mk_rest_cheese",
            "mk_rest_tapas",
            "mk_rest_patisserie",
            "mk_rest_anniversary",
            "mk_rest_mediterranean",
            "mk_rest_eggs",
            "mk_rest_fruits",
            "mk_restaurant_vegetarian",
            "mk_rest_apple",
            "mk_rest_icecream",
//            bebidas
            "mk_beb_bar",
            "mk_beb_beergarden",
            "mk_beb_coktail",
            "mk_beb_drink",
            "mk_beb_drugstore",
            "mk_beb_grocery",
            "mk_beb_liquor",
            "mk_beb_milk_bottle",
            "mk_beb_winebar",
            "mk_beb_winetasting",
            "mk_beb_coffee",


            "mk_shop_administration",
            "mk_shop_babymarkt",
            "mk_shop_bags",
            "mk_shop_clothers_female",
            "mk_shop_bollie",
            "mk_shop_clothers_male",
            "mk_shop_conveniencestore",
            "mk_shop_craftstore",
            "mk_shop_cycling_feed",
            "mk_shop_departmentstore",
            "mk_shop_fishing",
            "mk_shop_fishingboat",
            "mk_shop_fishingstore",
            "mk_shop_highhills",
            "mk_shop_homecenter",
            "mk_shop_jewelry",
            "mk_shop_kiosk",
            "mk_shop_kitchen",
            "mk_shop_laundromat",
            "mk_shop_library",
            "mk_shop_lingerie",
            "mk_shop_love_date",
            "mk_shop_loveinterest",
            "mk_shop_mall",
            "mk_shop_market",
            "mk_shop_medicalstore",
            "mk_shop_movierental",
            "mk_shop_ophthalmologist",
            "mk_shop_paintball",
            "mk_shop_perfumery",
            "mk_shop_photography",
            "mk_shop_picnic",
            "mk_shop_price_tag",
            "mk_shop_sauna",
            "mk_shop_shoes",
            "mk_shop_skis",
            "mk_shop_sledgerental",
            "mk_shop_sneakers",
            "mk_shop_snorkeling",
            "mk_shop_spa",
            "mk_shop_supermarket",
            "mk_shop_textiles",
            "mk_shop_tile_shop",
            "mk_shop_velocimeter",
            "mk_shop_video",
            "mk_shop_videogames",
            "mk_shop_wedding",
            "mk_shop_wiki",
            "mk_shop_wrestling",
            "mk_shop_youthhostel",


            "mk_fest_blast",
            "mk_fest_candy",
            "mk_fest_candy_cane",
            "mk_fest_christmasmarket",
            "mk_fest_circus",
            "mk_fest_comedyclub",
            "mk_fest_ferriswheel",
            "mk_fest_fireworks",
            "mk_fest_gifts",
            "mk_fest_magicshow",
            "mk_fest_party",
            "mk_fest_phantom",
            "mk_fest_santa",
            "mk_fest_theater",
            "mk_fest_themepark",
            "mk_festival",
//            juegos
            "mk_jue_bowling",
            "mk_jue_casino",
            "mk_jue_poker",

            "mk_obj_archeological",
            "mk_obj_art_museum",
            "mk_obj_artgallery",
            "mk_obj_battlefield",
            "mk_obj_beautifulview",
            "mk_obj_binoculars",
            "mk_obj_bomb",
            "mk_obj_brewery",
            "mk_obj_calendar",
            "mk_obj_childmuseum",
            "mk_obj_clock",
            "mk_obj_coins",
            "mk_obj_crafts",
            "mk_obj_crematorium",
            "mk_obj_curling",
            "mk_obj_dalben",
            "mk_obj_expert",
            "mk_obj_fire_hydrant",
            "mk_obj_fireexstinguisher",
            "mk_obj_firemen",
            "mk_obj_flag",
            "mk_obj_gas_cylinder",
            "mk_obj_glasses",
            "mk_obj_hats",
            "mk_obj_historical_museum",
            "mk_obj_hookah_final",
            "mk_obj_jacuzzi",
            "mk_obj_japanese_sake",
            "mk_obj_juice",
            "mk_obj_lodging",
            "mk_obj_map",
            "mk_obj_missile",
            "mk_obj_music_jazzclub",
            "mk_obj_music_live",
            "mk_obj_music_rock",
            "mk_obj_naval",
            "mk_obj_newsagent",
            "mk_obj_oyster",
            "mk_obj_pens",
            "mk_obj_pin",
            "mk_obj_pond",
            "mk_obj_postal",
            "mk_obj_poweroutage",
            "mk_obj_rescue",
            "mk_obj_school",
            "mk_obj_schreibwaren_web",
            "mk_obj_shootingrange",
            "mk_obj_ski_shoe",
            "mk_obj_slipway",
            "mk_obj_statue",
            "mk_obj_surface_metal",
            "mk_obj_tajine",
            "mk_obj_targ",
            "mk_obj_teahouse",
            "mk_obj_tebletennis",
            "mk_obj_text",
            "mk_obj_theft",
            "mk_obj_ticket_office",
            "mk_obj_tires",
            "mk_obj_umbrella",
            "mk_obj_university",
            "mk_obj_war",
            "mk_obj_beautysalon"

    };
/*
    private Integer[] construcciones = {

            R.raw.cons_apartment,
            R.raw.cons_barn,
            R.raw.cons_bunker,
            R.raw.cons_cabin,
            R.raw.cons_cafetaria,
            R.raw.cons_cathedral,
            R.raw.cons_chapel,
            R.raw.cons_church,
            R.raw.cons_earthquake,
            R.raw.cons_embassy,
            R.raw.cons_factory,
            R.raw.cons_farm,
            R.raw.cons_flood,
            R.raw.cons_historicalquarter,
            R.raw.cons_home, //   cabecera
            R.raw.cons_hospital_building,
            R.raw.cons_hostel,
            R.raw.cons_house,
            R.raw.cons_hut,
            R.raw.cons_japanese_temple,
            R.raw.cons_moderntower,
            R.raw.cons_mosquee,
            R.raw.cons_office_building,
            R.raw.cons_pagoda,
            R.raw.cons_palace,
            R.raw.cons_pleasurepier,
            R.raw.cons_rockhouse,
            R.raw.cons_sevilla,
            R.raw.cons_solarenergy,
            R.raw.cons_temple,
            R.raw.cons_templehindu,
            R.raw.cons_theravadapagoda,
            R.raw.cons_theravadatemple,
            R.raw.cons_townhouse,
            R.raw.cons_treedown,
            R.raw.cons_villa,
            R.raw.const_condominium,
            R.raw.const_conference,
            R.raw.const_congress,
            R.raw.const_convent,


            R.raw.lug_arch,
            R.raw.lug_beach,
            R.raw.lug_bed_breakfast,
            R.raw.lug_bigcity,
            R.raw.lug_castle,
            R.raw.lug_catholicgrave,
            R.raw.lug_cemetary,
            R.raw.lug_citywalls,
            R.raw.lug_cromlech,
            R.raw.lug_cropcircles,
            R.raw.lug_dam,
            R.raw.lug_fountain,
            R.raw.lug_ghosttown,
            R.raw.lug_japanese_lantern,
            R.raw.lug_jewishgrave,
            R.raw.lug_landmark,
            R.raw.lug_lifeguard,
            R.raw.lug_lighthouse,
            R.raw.lug_megalith,
            R.raw.lug_memorial,
            R.raw.lug_modernmonument,
            R.raw.lug_monument,
            R.raw.lug_motel,
            R.raw.lug_observatory,
            R.raw.lug_planetarium,
            R.raw.lug_playground,
            R.raw.lug_powerplant,
            R.raw.lug_powersubstation,
            R.raw.lug_pyramid,
            R.raw.lug_resort,
            R.raw.lug_ruins,
            R.raw.lug_shintoshrine,
            R.raw.lug_shipwreck,
            R.raw.lug_smallcity,
            R.raw.lug_spaceport,
            R.raw.lug_stadium,
            R.raw.lug_summercamp,
            R.raw.lug_swimming,
            R.raw.lug_synagogue,
            R.raw.lug_tollstation,
            R.raw.lug_tower,
            R.raw.lug_tunnel,
            R.raw.lug_watermill,
            R.raw.lug_waterwell,
            R.raw.lug_windmill,
            R.raw.lug_windturbine,
            R.raw.lug_worldwildway,
            R.raw.lug_museum_openair,
//nature
            R.raw.nat_agritourism,
            R.raw.nat_algae,
            R.raw.nat_avalanche,
            R.raw.nat_canyon,
            R.raw.nat_cave,
            R.raw.nat_cloudy,
            R.raw.nat_cloudysunny,
            R.raw.nat_desert,
            R.raw.nat_fallingrocks,
            R.raw.nat_fjord,
            R.raw.nat_flowers,
            R.raw.nat_forest,
            R.raw.nat_forest_two,
            R.raw.nat_geothermal_site,
            R.raw.nat_geyser,
            R.raw.nat_glacier,
            R.raw.nat_grass,
            R.raw.nat_lake,
            R.raw.nat_moonstar,
            R.raw.nat_mountains,
            R.raw.nat_mushroom,
            R.raw.nat_palm_tree,
            R.raw.nat_panoramicview,
            R.raw.nat_quadrifoglio,
            R.raw.nat_rainy,
            R.raw.nat_riparianhabitat,
            R.raw.nat_river,
            R.raw.nat_shore,
            R.raw.nat_thunderstorm,
            R.raw.nat_tornado,
            R.raw.nat_tsunami,
            R.raw.nat_vineyard,
            R.raw.nat_waterfall,
            R.raw.nat_wetlands,

    };

    private Integer[] personas = {

            R.raw.pers_beratungsstelle,
            R.raw.pers_billiard,
            R.raw.pers_bullfight,
            R.raw.pers_choral,
            R.raw.pers_communitycentre,
            R.raw.pers_construction,
            R.raw.pers_cramschool,
            R.raw.pers_crimescene,
            R.raw.pers_crossingguard,
            R.raw.pers_customs,
            R.raw.pers_dance_class,
            R.raw.pers_dancinghall,
            R.raw.pers_daycare,
            R.raw.pers_disability,
            R.raw.pers_dogs_leash,
            R.raw.pers_drinkingfountain,
            R.raw.pers_family,
            R.raw.pers_female,
            R.raw.pers_fitness,
            R.raw.pers_group,
            R.raw.pers_hiking,
            R.raw.pers_male, //  cabecera
            R.raw.pers_massage,
            R.raw.pers_mexican,
            R.raw.pers_music_hiphop,
            R.raw.pers_nanny,
            R.raw.pers_nursery,
            R.raw.pers_nursing_home,
            R.raw.pers_pedestriancrossing,
            R.raw.pers_petanque,
            R.raw.pers_prayer,
            R.raw.pers_prison,
            R.raw.pers_rollerskate,
            R.raw.pers_scubadiving,
            R.raw.pers_segway,
            R.raw.pers_seniorsite,
            R.raw.pers_shower,
            R.raw.pers_snowshoeing,
            R.raw.pers_strike,
            R.raw.pers_stripclub,
            R.raw.pers_therapy,
            R.raw.pers_toilets,
            R.raw.pers_trash,
            R.raw.pers_waiting,
            R.raw.pers_walkingtour,
            R.raw.pers_watercraft,
            R.raw.pers_waterpark,
            R.raw.pers_yoga,





            R.raw.pers_revolt,
            R.raw.pers_r_bouddha,
            R.raw.pers_zombie_outbreak,


            R.raw.dep_anchorpier,
            R.raw.dep_archery,
            R.raw.dep_badminton,
            R.raw.dep_baseball,
            R.raw.dep_basketball,
            R.raw.dep_beachvolleyball,
            R.raw.dep_bike_rising,
            R.raw.dep_boardercross,
            R.raw.dep_bobsleigh,
            R.raw.dep_boxing,
            R.raw.dep_climbing,
            R.raw.dep_cycling,
            R.raw.dep_cycling_sprint,
            R.raw.dep_diving,
            R.raw.dep_finish,
            R.raw.dep_golfing,
            R.raw.dep_gondola,
            R.raw.dep_handball,
            R.raw.dep_horseriding,
            R.raw.dep_hunting,
            R.raw.dep_icehockey,
            R.raw.dep_iceskating,
            R.raw.dep_jogging,
            R.raw.dep_judo,
            R.raw.dep_karate,
            R.raw.dep_kitesurfing,
            R.raw.dep_mountainbiking,
            R.raw.dep_nordicski,
            R.raw.dep_paragliding,
            R.raw.dep_parasailing,
            R.raw.dep_speedriding,
            R.raw.dep_ropescourse,
            R.raw.dep_skiing,
            R.raw.dep_skijump,
            R.raw.dep_skilifting,
            R.raw.dep_sledge,
            R.raw.dep_sledge_summer,
            R.raw.dep_snowboarding,
            R.raw.dep_snowpark_arc,
            R.raw.dep_soccer,
            R.raw.dep_spelunking,
            R.raw.dep_squash,
            R.raw.dep_sumo,
            R.raw.dep_surfacelift,
            R.raw.dep_surfing,
            R.raw.dep_taekwondo,
            R.raw.dep_tennis,
            R.raw.dep_volleyball,
            R.raw.dep_weights,

//          canoas
            R.raw.dep_windsurfing,
            R.raw.dep_waterskiing,
            R.raw.dep_surfpaddle,
            R.raw.dep_kayaking,
            R.raw.dep_rowboat,

//            motor
            R.raw.dep_snowmobiling,
            R.raw.dep_motorbike,

            R.raw.dep_karting,

            R.raw.dep_start_race,

            R.raw.dep_usfootball,
            R.raw.dep_rugbyfield,
            R.raw.dep_olympicsite,
            R.raw.dep_indoor_arena,
            R.raw.dep_cricket,
            R.raw.dep_cup,
            R.raw.dep_australianfootball,

//animales
            R.raw.ani_elephants,
            R.raw.ani_alligator,
            R.raw.ani_deer,
            R.raw.ani_tiger,
            R.raw.ani_dinopark,

            R.raw.ani_frog,
            R.raw.ani_har,
            R.raw.ani_monkey,
            R.raw.ani_penguin,
            R.raw.ani_pets,
            R.raw.ani_pig,
            R.raw.ani_rodent,
            R.raw.ani_seals,
            R.raw.ani_shark,
            R.raw.ani_snail,
            R.raw.ani_snakes,
            R.raw.ani_turtle,
            R.raw.ani_corral,
            R.raw.ani_cow,
            R.raw.ani_wildlifecrossing,
            R.raw.ani_toys,
            R.raw.ani_veterinary,
//
//          peces
            R.raw.ani_dolphins,
            R.raw.ani_fish,
            R.raw.ani_fishchips,
            R.raw.ani_deepseafishing,
            R.raw.ani_whale,
            R.raw.ani_african,
            R.raw.ani_aquarium,


//          aves
            R.raw.ani_birds,
            R.raw.ani_tweet,
            R.raw.ani_duck,
            R.raw.ani_chicken,
            R.raw.ani_penguin,

            R.raw.ani_bats,

//            insectos
            R.raw.ani_lobster,
            R.raw.ani_mosquito,
            R.raw.ani_ant,
            R.raw.ani_spider,

            R.raw.ani_butterfly,




            R.raw.ani_cowabduction,
            R.raw.ani_zoo,
            R.raw.ani_shelter,
            R.raw.ani_farmstand
    };

    private Integer[] objetos = {


    };


    private Integer[] vehiculos = {

//            aviones
            R.raw.veh_airport_apron,
            R.raw.veh_airport_runway,
            R.raw.veh_airport_terminal,
            R.raw.veh_airshow,
            R.raw.veh_jetfighter,
            R.raw.veh_kingair,
            R.raw.veh_planecrash,
            R.raw.veh_travel_agency,
            R.raw.veh_trolley,
            R.raw.veh_helicopter,


            R.raw.veh_hotairbaloon,
            R.raw.veh_ufo,

//            cuatro ruedas
            R.raw.veh_steamtrain,
            R.raw.veh_train,
            R.raw.veh_tramway,
            R.raw.veh_underground,

            R.raw.veh_bus,
            R.raw.veh_busstop,
            R.raw.veh_bustour,
            R.raw.veh_cablecar,
            R.raw.veh_camping,
            R.raw.veh_funicolar,
            R.raw.veh_plowtruck,
            R.raw.veh_truck,
            R.raw.veh_van,
            R.raw.veh_warehouse,

            R.raw.veh_car, // cabecera
            R.raw.veh_car_share,
            R.raw.veh_caraccident,
            R.raw.veh_carrental,
            R.raw.veh_carwash,
            R.raw.veh_convertible,
            R.raw.veh_descent,
            R.raw.veh_ford,
            R.raw.veh_fourbyfour,
            R.raw.veh_icy_road,
            R.raw.veh_jeep,
            R.raw.veh_pickup,
            R.raw.veh_pickup_camper,
            R.raw.veh_sportscar,
            R.raw.veh_sportutilityvehicle,
            R.raw.veh_ambulance,
            R.raw.veh_taxi,

//            tractores
            R.raw.veh_bulldozer,
            R.raw.veh_military,
            R.raw.veh_field,


//            motos
            R.raw.veh_atv,
            R.raw.veh_ducati_diavel,
            R.raw.veh_vespa,


            R.raw.veh_bicycle_shop,
            R.raw.veh_bike_downhill,

//            barcos
            R.raw.veh_battleship,
            R.raw.veh_boat,
            R.raw.veh_boatcrane,
            R.raw.veh_cruiseship,
            R.raw.veh_ferry,
            R.raw.veh_marina,
            R.raw.veh_oilrig,
            R.raw.veh_solar_cruise,
            R.raw.veh_taxiboat,
            R.raw.veh_waterway_canal,


            R.raw.veh_submarine,

            R.raw.veh_kayak,

            R.raw.veh_harbor,


            R.raw.veh_horse_drawn_carriage,

//            elementos ruteros
            R.raw.veh_fillingstation,
            R.raw.veh_oil,

            R.raw.veh_barrier,
            R.raw.veh_closedroad,
            R.raw.veh_highway,
            R.raw.veh_road,
            R.raw.veh_roadtype_gravel,

            R.raw.veh_bridge_modern,
            R.raw.veh_bridge_old,
            R.raw.veh_trafficcamera,
            R.raw.veh_trafficlight,


    };

    private Integer[] simbolos = {


            R.raw.work_barber,
            R.raw.work_compost,
            R.raw.work_constructioncrane,
            R.raw.work_contract,
            R.raw.work_court,
            R.raw.work_dentist,
            R.raw.work_drinkingwater,
            R.raw.work_elevator,
            R.raw.work_elevator_down,
            R.raw.work_findajob,
            R.raw.work_flagman,
            R.raw.work_foundry,
            R.raw.work_haybale,
            R.raw.work_industry, //  cabecera
            R.raw.work_laboratory,
            R.raw.work_landfill,
            R.raw.work_levelcrossing,
            R.raw.work_linedown,
            R.raw.work_lock,
            R.raw.work_lockerrental,
            R.raw.work_mastcrane,
            R.raw.work_mine,
            R.raw.work_mobilephonetower,
            R.raw.work_moving_walkway_entert,
            R.raw.work_mural,
            R.raw.work_oido,
            R.raw.work_oilpumpjack,
            R.raw.work_olfass,
            R.raw.work_paint,
            R.raw.work_pick_stand,
            R.raw.work_powerlinepole,
            R.raw.work_presentation,
            R.raw.work_repair,
            R.raw.work_sawmill,
            R.raw.work_septic_tank,
            R.raw.work_tailor,
            R.raw.work_tools,
            R.raw.work_treasure_chest,
            R.raw.work_water,
            R.raw.work_waterfilter,
            R.raw.work_water,
            R.raw.work_waterwellpump,
            R.raw.work_welding,
            R.raw.work_workoffice,
            R.raw.work_workshop,







            R.raw.tec_anthropo,
            R.raw.tec_atm,
            R.raw.tec_audio,
            R.raw.tec_bike_charging,
            R.raw.tec_bike_rental,
            R.raw.tec_cctv,
            R.raw.tec_cinema,
            R.raw.tec_computers,
            R.raw.tec_database,
            R.raw.tec_degrees,
            R.raw.tec_folder,
            R.raw.tec_metronetwork,
            R.raw.tec_music,
            R.raw.tec_phones,
            R.raw.tec_photo,
            R.raw.tec_printer,
            R.raw.tec_qr_code,
            R.raw.tec_radar,
            R.raw.tec_radio_control,
            R.raw.tec_radio_station,
            R.raw.tec_tortillas,
            R.raw.tec_webcam,

            R.raw.sim_aboriginal,
            R.raw.sim_accesdenied,
            R.raw.sim_acupuncture,
            R.raw.sim_administrativeboundary,
            R.raw.sim_aed,
            R.raw.sim_alien,
            R.raw.sim_amphitheater,
            R.raw.sim_amphitheater_two,
            R.raw.sim_anemometer_mono,
            R.raw.sim_army,
            R.raw.sim_award,
            R.raw.sim_bank,
            R.raw.sim_campfire,
            R.raw.sim_caution,
            R.raw.sim_channelchange,
            R.raw.sim_chart,
            R.raw.sim_chemistry,
            R.raw.sim_chiropractor,
            R.raw.sim_citysquare,
            R.raw.sim_comment,
            R.raw.sim_company,
            R.raw.sim_conversation,
            R.raw.sim_country,
            R.raw.sim_cross,
            R.raw.sim_cserkesz_ikon,
            R.raw.sim_currencyexchange,
            R.raw.sim_curveleft,
            R.raw.sim_diagonal_reverse,
            R.raw.sim_direction_down,
            R.raw.sim_direction_uturn,
            R.raw.sim_doublebendright,
            R.raw.sim_downloadicon,
            R.raw.sim_emblem,
            R.raw.sim_entrance,
            R.raw.sim_exchequer,
            R.raw.sim_exit,
            R.raw.sim_fire,
            R.raw.sim_firstaid,
            R.raw.sim_footprint,
            R.raw.sim_fossils,
            R.raw.sim_freqchg,
            R.raw.sim_gay_male,
            R.raw.sim_gay_female,
            R.raw.sim_geocaching,
            R.raw.sim_headstone,
            R.raw.sim_helipad,
            R.raw.sim_highschool,
            R.raw.sim_honeycomb,
            R.raw.sim_hotel,
            R.raw.sim_icon,
            R.raw.sim_indian,
            R.raw.sim_information,
            R.raw.sim_iobridge,
            R.raw.sim_jewishquarter,
            R.raw.sim_junction,
            R.raw.sim_kebab,
            R.raw.sim_mainroad,
            R.raw.sim_mapicon,
            R.raw.sim_maxheight,
            R.raw.sim_maxweight,
            R.raw.sim_maxwidth,
            R.raw.sim_medicine,
            R.raw.sim_metano,
            R.raw.sim_monument_historique,
            R.raw.sim_music_classical,
            R.raw.sim_no_nuke,
            R.raw.sim_notvisited,
            R.raw.sim_outlet,
            R.raw.sim_parkandride,
            R.raw.sim_parking_bicycle,
            R.raw.sim_parking_meter,
            R.raw.sim_parkinggarage,
            R.raw.sim_peace,
            R.raw.sim_petroglyphs,
            R.raw.sim_pirates,
            R.raw.sim_police,
            R.raw.sim_publicart,
            R.raw.sim_radiation,
            R.raw.sim_reatorlogowhite,
            R.raw.sim_recycle,
            R.raw.sim_regroup,
            R.raw.sim_science,
            R.raw.sim_scoutgroup,
            R.raw.sim_share,
            R.raw.sim_sight,
            R.raw.sim_signpost,
            R.raw.sim_sikh,
            R.raw.sim_skull,
            R.raw.sim_smiley_happy,
            R.raw.sim_smoking,
            R.raw.sim_snowy,
            R.raw.sim_sozialeeinrichtung,
            R.raw.sim_speed,
            R.raw.sim_speedhump,
            R.raw.sim_splice,
            R.raw.sim_square_compass,
            R.raw.sim_st_margarets_cross,
            R.raw.sim_stairs,
            R.raw.sim_star,
            R.raw.sim_stargate_raw,
            R.raw.sim_stop,
            R.raw.sim_sunny,
            R.raw.sim_sunsetland,
            R.raw.sim_surveying,
            R.raw.sim_taxiway,
            R.raw.sim_thecapturelab,
            R.raw.sim_three_d,
            R.raw.sim_tidaldiamond,
            R.raw.sim_treasure_mark,
            R.raw.sim_triskelion,
            R.raw.sim_tv,
            R.raw.sim_up,
            R.raw.sim_wendepunkt,
            R.raw.sim_wifi,
            R.raw.sim_woodshed,
            R.raw.sim_world,
            R.raw.sim_worldheritagesite,
            R.raw.sim_you_are_here,
            R.raw.sim_zoom,
    };*/
    }


