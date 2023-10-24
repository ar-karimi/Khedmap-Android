package com.khedmap.khedmap.di.module;

import com.khedmap.khedmap.LoginSignUp.Model.EnterMobileModel;
import com.khedmap.khedmap.LoginSignUp.Model.NameRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.ProfilePicGenderRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.SplashModel;
import com.khedmap.khedmap.LoginSignUp.Model.TryToConnectModel;
import com.khedmap.khedmap.LoginSignUp.Model.UpdateRequiredModel;
import com.khedmap.khedmap.LoginSignUp.Model.ValidateMobileModel;
import com.khedmap.khedmap.Order.Model.CommentModel;
import com.khedmap.khedmap.Order.Model.EditFavoriteLocationModel;
import com.khedmap.khedmap.Order.Model.FavoriteExpertModel;
import com.khedmap.khedmap.Order.Model.FavoriteLocationModel;
import com.khedmap.khedmap.Order.Model.GetDetailedAddressModel;
import com.khedmap.khedmap.Order.Model.GetOrderDateTimeModel;
import com.khedmap.khedmap.Order.Model.HomeModel;
import com.khedmap.khedmap.Order.Model.IncreaseCreditModel;
import com.khedmap.khedmap.Order.Model.InfiniteCategoryModel;
import com.khedmap.khedmap.Order.Model.MapsModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.CanceledStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.CompletedStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.DoingStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.HangingStateModel;
import com.khedmap.khedmap.Order.Model.OrderDetail.WaitingForStartStateModel;
import com.khedmap.khedmap.Order.Model.OrderSpecificationModel;
import com.khedmap.khedmap.Order.Model.OrdersManagementModel;
import com.khedmap.khedmap.Order.Model.ProfileModel;
import com.khedmap.khedmap.Order.Model.QuestionsModel;
import com.khedmap.khedmap.Order.Model.SearchModel;
import com.khedmap.khedmap.Order.Model.GetDistrictModel;
import com.khedmap.khedmap.Order.Model.SubCategoryModel;
import com.khedmap.khedmap.Order.Model.SuggestionDetailModel;
import com.khedmap.khedmap.Order.Model.SuggestionsModel;
import com.khedmap.khedmap.Order.Model.VerifyFinishOrderModel;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    public EnterMobileModel provideEnterMobileModel() {
        return new EnterMobileModel();
    }

    @Provides
    public SplashModel provideSplashModel() {
        return new SplashModel();
    }

    @Provides
    public UpdateRequiredModel provideUpdateRequiredModel() {
        return new UpdateRequiredModel();
    }

    @Provides
    public TryToConnectModel provideTryToConnectModel() {
        return new TryToConnectModel();
    }

    @Provides
    public NameRegisterModel NameRegisterModel() {
        return new NameRegisterModel();
    }

    @Provides
    public ValidateMobileModel ValidateMobileModel() {
        return new ValidateMobileModel();
    }

    @Provides
    public ProfilePicGenderRegisterModel ProfilePicGenderRegisterModel() {
        return new ProfilePicGenderRegisterModel();
    }

    @Provides
    public HomeModel HomeModel() {
        return new HomeModel();
    }

    @Provides
    public InfiniteCategoryModel InfiniteCategoryModel() {
        return new InfiniteCategoryModel();
    }

    @Provides
    public SubCategoryModel SubCategoryModel() {
        return new SubCategoryModel();
    }

    @Provides
    public OrderSpecificationModel OrderSpecificationModel() {
        return new OrderSpecificationModel();
    }

    @Provides
    public QuestionsModel QuestionsModel() {
        return new QuestionsModel();
    }

    @Provides
    public GetOrderDateTimeModel GetOrderDateTimeModel() {
        return new GetOrderDateTimeModel();
    }

    @Provides
    public GetDistrictModel GetDistrictModel() {
        return new GetDistrictModel();
    }

    @Provides
    public SuggestionsModel SuggestionsModel() {
        return new SuggestionsModel();
    }

    @Provides
    public SuggestionDetailModel SuggestionDetailModel() {
        return new SuggestionDetailModel();
    }

    @Provides
    public GetDetailedAddressModel GetDetailedAddressModel() {
        return new GetDetailedAddressModel();
    }

    @Provides
    public OrdersManagementModel OrdersManagementModel() {
        return new OrdersManagementModel();
    }

    @Provides
    public HangingStateModel HangingStateModel() {
        return new HangingStateModel();
    }

    @Provides
    public WaitingForStartStateModel WaitingForStartStateModel() {
        return new WaitingForStartStateModel();
    }

    @Provides
    public CanceledStateModel CanceledStateModel() {
        return new CanceledStateModel();
    }

    @Provides
    public DoingStateModel DoingStateModel() {
        return new DoingStateModel();
    }

    @Provides
    public CompletedStateModel CompletedStateModel() {
        return new CompletedStateModel();
    }

    @Provides
    public VerifyFinishOrderModel VerifyFinishOrderModel() {
        return new VerifyFinishOrderModel();
    }

    @Provides
    public MapsModel MapsModel() {
        return new MapsModel();
    }

    @Provides
    public CommentModel CommentModel() {
        return new CommentModel();
    }

    @Provides
    public SearchModel SearchModel() {
        return new SearchModel();
    }

    @Provides
    public ProfileModel ProfileModel() {
        return new ProfileModel();
    }

    @Provides
    public IncreaseCreditModel IncreaseCreditModel() {
        return new IncreaseCreditModel();
    }

    @Provides
    public FavoriteExpertModel FavoriteExpertModel() {
        return new FavoriteExpertModel();
    }

    @Provides
    public FavoriteLocationModel FavoriteLocationModel() {
        return new FavoriteLocationModel();
    }

    @Provides
    public EditFavoriteLocationModel EditFavoriteLocationModel() {
        return new EditFavoriteLocationModel();
    }

}