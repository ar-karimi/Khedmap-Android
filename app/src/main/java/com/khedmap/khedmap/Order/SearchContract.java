package com.khedmap.khedmap.Order;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Response;
import com.khedmap.khedmap.Order.DataModels.SearchResult;

import java.util.List;

public interface SearchContract {

    interface ViewCallBack {

        void showSnackBar(String message);

        void showSearchLoadingView(boolean showSearchLoadingView);

        void showLoadingView(boolean showLoadingView);

        void showSearchResultsRecyclerView(List<SearchResult> searchResults);

        void setNoItemToShowVisible();

        void setResultListVisible();

        void hideItems();

        void showBackgroundImage();

        void showClearIcon(boolean showIcon);
    }


    interface ModelCallBack {

        void setApiTokenSharedPref(String apiToken, Context context);

        String getApiTokenSharedPref(Context context);

        void sendSearchRequest(Context context, final String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);

        void cancelRequest();

        void sendCheckCategoryChildrenRequest(Context context, String requestBody, Response.Listener responseListener, Response.ErrorListener errorListener);
    }


    interface PresenterCallBack {

        void setActivityContext(Activity activity);

        void textChanged(String input);

        void searchResultItemClicked(SearchResult item, Activity activity);
    }

}
