package com.bunny.entertainment.yousee.utils;

import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeAndQualityModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;

import java.util.ArrayList;

public class DataHolder {

    //for DramaFragment
    private static ArrayList<DramaListModel> topAiringDramaListModelArrayList;
    private static ArrayList<DramaListModel> topShowsDramaListModelArrayList;
    private static ArrayList<DramaListModel> popularShowsDramaListModelArrayList;
    private static ArrayList<DramaListModel> upcomingShowsDramaListModelArrayList;
    private static ArrayList<DramaListModel> varietyShowsDramaListModelArrayList;
    private static ArrayList<DramaListModel> topMoviesDramaListModelArrayList;
    private static ArrayList<DramaListModel> popularMoviesDramaListModelArrayList;
    private static ArrayList<DramaListModel> newestMoviesDramaListModelArrayList;
    private static ArrayList<DramaListModel> upcomingMoviesDramaListModelArrayList;
    private static ArrayList<DramaListModel> viewAllWorkArrayList;
    private static ArrayList<String> peopleWorkIdModelArrayList;
    private static ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList;



    public static ArrayList<DramaListModel> getTopAiringDramaListModelArrayList() {
        return topAiringDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getTopShowsDramaListModelArrayList() {
        return topShowsDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getPopularShowsDramaListModelArrayList() {
        return popularShowsDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getUpcomingShowsDramaListModelArrayList() {
        return upcomingShowsDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getVarietyShowsDramaListModelArrayList() {
        return varietyShowsDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getTopMoviesDramaListModelArrayList() {
        return topMoviesDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getPopularMoviesDramaListModelArrayList() {
        return popularMoviesDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getNewestMoviesDramaListModelArrayList() {
        return newestMoviesDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getUpcomingMoviesDramaListModelArrayList() {
        return upcomingMoviesDramaListModelArrayList;
    }
    public static ArrayList<DramaListModel> getViewAllWorkArrayList() {
        return viewAllWorkArrayList;
    }
    public static ArrayList<String> getPeopleWorkIdModelArrayList() {
        return peopleWorkIdModelArrayList;
    }
    public static ArrayList<DramaListModel> getContinueWatchingDramaDetailsArrayList() {
        return continueWatchingDramaDetailsArrayList;
    }



    public static void setTopAiringDramaListModelArrayList(ArrayList<DramaListModel> list) {
        topAiringDramaListModelArrayList = list;
    }
    public static void setTopShowsDramaListModelArrayList(ArrayList<DramaListModel> list) {
        topShowsDramaListModelArrayList = list;
    }
    public static void setPopularShowsDramaListModelArrayList(ArrayList<DramaListModel> list) {
        popularShowsDramaListModelArrayList = list;
    }
    public static void setUpcomingShowsDramaListModelArrayList(ArrayList<DramaListModel> list) {
        upcomingShowsDramaListModelArrayList = list;
    }
    public static void setVarietyShowsDramaListModelArrayList(ArrayList<DramaListModel> list) {
        varietyShowsDramaListModelArrayList = list;
    }
    public static void setTopMoviesDramaListModelArrayList(ArrayList<DramaListModel> list) {
        topMoviesDramaListModelArrayList = list;
    }
    public static void setPopularMoviesDramaListModelArrayList(ArrayList<DramaListModel> list) {
        popularMoviesDramaListModelArrayList = list;
    }
    public static void setNewestMoviesDramaListModelArrayList(ArrayList<DramaListModel> list) {
        newestMoviesDramaListModelArrayList = list;
    }
    public static void setUpcomingMoviesDramaListModelArrayList(ArrayList<DramaListModel> list) {
        upcomingMoviesDramaListModelArrayList = list;
    }
    public static void setViewAllWorkArrayList(ArrayList<DramaListModel> list) {
        viewAllWorkArrayList = list;
    }
    public static void setPeopleWorkIdModelArrayList(ArrayList<String> list) {
        peopleWorkIdModelArrayList = list;
    }
    public static void setContinueWatchingDramaDetailsArrayList(ArrayList<DramaListModel> list) {
        continueWatchingDramaDetailsArrayList = list;
    }




    //for ShowsFragment
    private static ArrayList<ShowsListModel> popularShowsListModelArrayList;
    private static ArrayList<ShowsListModel> topRatedShowsListModelArrayList;
    private static ArrayList<ShowsListModel> onTheAirShowsListModelArrayList;
    private static ArrayList<ShowsListModel> popularMoviesShowsListModelArrayList;
    private static ArrayList<ShowsListModel> topRatedMoviesShowsListModelArrayList;
    private static ArrayList<ShowsListModel> nowOnCinemasMoviesShowsListModelArrayList;
    private static ArrayList<ShowsListModel> upcomingMoviesShowsListModelArrayList;


    public static ArrayList<ShowsListModel> getPopularShowsListModelArrayList() {
        return popularShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getTopRatedShowsListModelArrayList() {
        return topRatedShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getOnTheAirShowsListModelArrayList() {
        return onTheAirShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getPopularMoviesListModelArrayList() {
        return popularMoviesShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getTopRatedMoviesListModelArrayList() {
        return topRatedMoviesShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getNowOnCinemasMoviesListModelArrayList() {
        return nowOnCinemasMoviesShowsListModelArrayList;
    }
    public static ArrayList<ShowsListModel> getUpcomingMoviesListModelArrayList() {
        return nowOnCinemasMoviesShowsListModelArrayList;
    }


    public static void setPopularShowsListModelArrayList(ArrayList<ShowsListModel> list) {
        popularShowsListModelArrayList = list;
    }
    public static void setTopRatedShowsListModelArrayList(ArrayList<ShowsListModel> list) {
        topRatedShowsListModelArrayList = list;
    }
    public static void setOnTheAirShowsListModelArrayList(ArrayList<ShowsListModel> list) {
        onTheAirShowsListModelArrayList = list;
    }
    public static void setPopularMoviesListModelArrayList(ArrayList<ShowsListModel> list) {
        popularMoviesShowsListModelArrayList = list;
    }
    public static void setTopRatedMoviesListModelArrayList(ArrayList<ShowsListModel> list) {
        topRatedMoviesShowsListModelArrayList = list;
    }
    public static void setNowOnCinemasMoviesListModelArrayList(ArrayList<ShowsListModel> list) {
        nowOnCinemasMoviesShowsListModelArrayList = list;
    }
    public static void setUpcomingMoviesListModelArrayList(ArrayList<ShowsListModel> list) {
        nowOnCinemasMoviesShowsListModelArrayList = list;
    }


    //for AnimeFragment
    private static ArrayList<AnimeListModel> TopAiringAnimeListModelArrayList;
    private static ArrayList<AnimeListModel> TopRatedTvSeriesAnimeListModelArrayList;
    private static ArrayList<AnimeListModel> TopRatedMoviesAnimeListModelArrayList;
    private static ArrayList<AnimeListModel> PopularAnimeListModelArrayList;
    private static ArrayList<AnimeListModel> TopUpcomingAnimeListModelArrayList;

    public static ArrayList<AnimeListModel> getTopAiringAnimeListModelArrayList() {
        return TopAiringAnimeListModelArrayList;
    }
    public static ArrayList<AnimeListModel> getTopRatedTvSeriesAnimeListModelArrayList() {
        return TopRatedTvSeriesAnimeListModelArrayList;
    }
    public static ArrayList<AnimeListModel> getTopRatedMoviesAnimeListModelArrayList() {
        return TopRatedMoviesAnimeListModelArrayList;
    }
    public static ArrayList<AnimeListModel> getPopularAnimeListModelArrayList() {
        return PopularAnimeListModelArrayList;
    }
    public static ArrayList<AnimeListModel> getTopUpcomingAnimeListModelArrayList() {
        return TopUpcomingAnimeListModelArrayList;
    }


    public static void setTopAiringAnimeListModelArrayList(ArrayList<AnimeListModel> topAiringAnimeListModelArrayList) {
        TopAiringAnimeListModelArrayList = topAiringAnimeListModelArrayList;
    }
    public static void setTopRatedTvSeriesAnimeListModelArrayList(ArrayList<AnimeListModel> topRatedTvSeriesAnimeListModelArrayList) {
        TopRatedTvSeriesAnimeListModelArrayList = topRatedTvSeriesAnimeListModelArrayList;
    }
    public static void setTopRatedMoviesAnimeListModelArrayList(ArrayList<AnimeListModel> topRatedMoviesAnimeListModelArrayList) {
        TopRatedMoviesAnimeListModelArrayList = topRatedMoviesAnimeListModelArrayList;
    }
    public static void setPopularAnimeListModelArrayList(ArrayList<AnimeListModel> popularAnimeListModelArrayList) {
        PopularAnimeListModelArrayList = popularAnimeListModelArrayList;
    }
    public static void setTopUpcomingAnimeListModelArrayList(ArrayList<AnimeListModel> topUpcomingAnimeListModelArrayList) {
        TopUpcomingAnimeListModelArrayList = topUpcomingAnimeListModelArrayList;
    }


    /////for anime episode links
    private static ArrayList<AnimeEpisodeAndQualityModel> animeEpisodeAndQualityModelArrayList;

    public static ArrayList<AnimeEpisodeAndQualityModel> getAnimeEpisodeAndQualityModelArrayList() {
        return animeEpisodeAndQualityModelArrayList;
    }

    public static void setAnimeEpisodeAndQualityModelArrayList(ArrayList<AnimeEpisodeAndQualityModel> animeEpisodeAndQualityModelArrayList) {
        DataHolder.animeEpisodeAndQualityModelArrayList = animeEpisodeAndQualityModelArrayList;
    }
}
