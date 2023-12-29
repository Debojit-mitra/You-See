package com.bunny.entertainment.yousee.utils;

public class Constants {
    public static final String TMDB_ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3NWFhYWNmYzU4MmQwZTMwM2YzZWY5NTMyNmFmNjY5ZCIsInN1YiI6IjYxNzU2ODMwMWQwMTkxMDAyZDdhYjhjNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.mDq-4Igz1VPSHe5YVYC5mj_1-9OsUtjJ3tRN-WMdSF8";
    public static final String TMDB_URL_MAIN = "https://api.themoviedb.org/3/";
    public static String MY_CONSUMET_API = "https://entertainment-bunny-yousee.koyeb.app/";
    public static String API_CHECK_UPDATER_URL = "https://api.npoint.io/eebb92c0742954eee80f";
    public static String API_GENRES_URL = "https://api.npoint.io/ca2d69822c6fe6399ac6";
    public static final int MAX_GROUP_SIZE = 25;
    public static String MY_DRAMALIST_URL = "https://mydramalist.com";
    public static String MYANIMELIST_URL = "https://myanimelist.net";
    public static String ANILIST_URL = "https://anilist.co/";
    public static String RANDOM_WALLPAPER_300DP = "https://source.unsplash.com/random/300x200/?movies";


    //animeFragment
    public static String topAiringThisSeason = MYANIMELIST_URL + "/anime/season"; // /2023/fall
    public static String topAiringPreviousSeason = MYANIMELIST_URL + "/anime/season/2023/fall";
    public static String topAiringUpcomingSeason = MYANIMELIST_URL + "/anime/season/2024/spring";


    //update methods
    public static void updateMyConsumetApi(String value) {
        MY_CONSUMET_API = value;
    }

    public static void updateApiGenresUrl(String value) {
        API_GENRES_URL = value;
    }
}
