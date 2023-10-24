package com.khedmap.khedmap.di.module;

import com.khedmap.khedmap.LoginSignUp.EnterMobileContract;
import com.khedmap.khedmap.LoginSignUp.Model.EnterMobileModel;
import com.khedmap.khedmap.LoginSignUp.Model.NameRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.ProfilePicGenderRegisterModel;
import com.khedmap.khedmap.LoginSignUp.Model.SplashModel;
import com.khedmap.khedmap.LoginSignUp.Model.TryToConnectModel;
import com.khedmap.khedmap.LoginSignUp.Model.UpdateRequiredModel;
import com.khedmap.khedmap.LoginSignUp.Model.ValidateMobileModel;
import com.khedmap.khedmap.LoginSignUp.NameRegisterContract;
import com.khedmap.khedmap.LoginSignUp.Presenter.EnterMobilePresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.NameRegisterPresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.ProfilePicGenderRegisterPresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.SplashPresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.TryToConnectPresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.UpdateRequiredPresenter;
import com.khedmap.khedmap.LoginSignUp.Presenter.ValidateMobilePresenter;
import com.khedmap.khedmap.LoginSignUp.ProfilePicGenderRegisterContract;
import com.khedmap.khedmap.LoginSignUp.SplashContract;
import com.khedmap.khedmap.LoginSignUp.TryToConnectContract;
import com.khedmap.khedmap.LoginSignUp.UpdateRequiredContract;
import com.khedmap.khedmap.LoginSignUp.ValidateMobileContract;
import com.khedmap.khedmap.Order.CanceledStateContract;
import com.khedmap.khedmap.Order.CommentContract;
import com.khedmap.khedmap.Order.CompletedStateContract;
import com.khedmap.khedmap.Order.DoingStateContract;
import com.khedmap.khedmap.Order.EditFavoriteLocationContract;
import com.khedmap.khedmap.Order.FavoriteExpertContract;
import com.khedmap.khedmap.Order.FavoriteLocationContract;
import com.khedmap.khedmap.Order.GetDetailedAddressContract;
import com.khedmap.khedmap.Order.GetOrderDateTimeContract;
import com.khedmap.khedmap.Order.HangingStateContract;
import com.khedmap.khedmap.Order.HomeContract;
import com.khedmap.khedmap.Order.IncreaseCreditContract;
import com.khedmap.khedmap.Order.InfiniteCategoryContract;
import com.khedmap.khedmap.Order.MapsContract;
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
import com.khedmap.khedmap.Order.OrderSpecificationContract;
import com.khedmap.khedmap.Order.OrdersManagementContract;
import com.khedmap.khedmap.Order.Presenter.CommentPresenter;
import com.khedmap.khedmap.Order.Presenter.EditFavoriteLocationPresenter;
import com.khedmap.khedmap.Order.Presenter.FavoriteExpertPresenter;
import com.khedmap.khedmap.Order.Presenter.FavoriteLocationPresenter;
import com.khedmap.khedmap.Order.Presenter.GetDetailedAddressPresenter;
import com.khedmap.khedmap.Order.Presenter.GetOrderDateTimePresenter;
import com.khedmap.khedmap.Order.Presenter.HomePresenter;
import com.khedmap.khedmap.Order.Presenter.IncreaseCreditPresenter;
import com.khedmap.khedmap.Order.Presenter.InfiniteCategoryPresenter;
import com.khedmap.khedmap.Order.Presenter.MapsPresenter;
import com.khedmap.khedmap.Order.Presenter.OrderDetail.CanceledStatePresenter;
import com.khedmap.khedmap.Order.Presenter.OrderDetail.CompletedStatePresenter;
import com.khedmap.khedmap.Order.Presenter.OrderDetail.DoingStatePresenter;
import com.khedmap.khedmap.Order.Presenter.OrderDetail.HangingStatePresenter;
import com.khedmap.khedmap.Order.Presenter.OrderDetail.WaitingForStartStatePresenter;
import com.khedmap.khedmap.Order.Presenter.OrderSpecificationPresenter;
import com.khedmap.khedmap.Order.Presenter.OrdersManagementPresenter;
import com.khedmap.khedmap.Order.Presenter.ProfilePresenter;
import com.khedmap.khedmap.Order.Presenter.QuestionsPresenter;
import com.khedmap.khedmap.Order.Presenter.SearchPresenter;
import com.khedmap.khedmap.Order.Presenter.GetDistrictPresenter;
import com.khedmap.khedmap.Order.Presenter.SubCategoryPresenter;
import com.khedmap.khedmap.Order.Presenter.SuggestionDetailPresenter;
import com.khedmap.khedmap.Order.Presenter.SuggestionsPresenter;
import com.khedmap.khedmap.Order.Presenter.VerifyFinishOrderPresenter;
import com.khedmap.khedmap.Order.ProfileContract;
import com.khedmap.khedmap.Order.QuestionsContract;
import com.khedmap.khedmap.Order.SearchContract;
import com.khedmap.khedmap.Order.GetDistrictContract;
import com.khedmap.khedmap.Order.SubCategoryContract;
import com.khedmap.khedmap.Order.SuggestionDetailContract;
import com.khedmap.khedmap.Order.SuggestionsContract;
import com.khedmap.khedmap.Order.VerifyFinishOrderContract;
import com.khedmap.khedmap.Order.WaitingForStartStateContract;

import dagger.Module;
import dagger.Provides;

@Module
public class MvpModule {

    private EnterMobileContract.ViewCallBack enterMobileViewCallBack;
    private SplashContract.ViewCallBack splashViewCallBack;
    private UpdateRequiredContract.ViewCallBack UpdateRequiredViewCallBack;
    private TryToConnectContract.ViewCallBack TryToConnectViewCallBack;
    private NameRegisterContract.ViewCallBack NameRegisterViewCallBack;
    private ValidateMobileContract.ViewCallBack ValidateMobileViewCallBack;
    private ProfilePicGenderRegisterContract.ViewCallBack ProfilePicGenderRegisterViewCallBack;
    private HomeContract.ViewCallBack HomeViewCallBack;
    private InfiniteCategoryContract.ViewCallBack InfiniteCategoryViewCallBack;
    private SubCategoryContract.ViewCallBack SubCategoryViewCallBack;
    private OrderSpecificationContract.ViewCallBack OrderSpecificationViewCallBack;
    private QuestionsContract.ViewCallBack QuestionsViewCallBack;
    private GetOrderDateTimeContract.ViewCallBack GetOrderDateTimeViewCallBack;
    private GetDistrictContract.ViewCallBack GetDistrictViewCallBack;
    private SuggestionsContract.ViewCallBack SuggestionsViewCallBack;
    private SuggestionDetailContract.ViewCallBack SuggestionDetailViewCallBack;
    private GetDetailedAddressContract.ViewCallBack GetDetailedAddressViewCallBack;
    private OrdersManagementContract.ViewCallBack OrdersManagementViewCallBack;
    private HangingStateContract.ViewCallBack HangingStateViewCallBack;
    private WaitingForStartStateContract.ViewCallBack WaitingForStartStateViewCallBack;
    private CanceledStateContract.ViewCallBack CanceledStateViewCallBack;
    private DoingStateContract.ViewCallBack DoingStateViewCallBack;
    private CompletedStateContract.ViewCallBack CompletedStateViewCallBack;
    private VerifyFinishOrderContract.ViewCallBack VerifyFinishOrderViewCallBack;
    private MapsContract.ViewCallBack MapsViewCallBack;
    private CommentContract.ViewCallBack CommentViewCallBack;
    private SearchContract.ViewCallBack SearchViewCallBack;
    private ProfileContract.ViewCallBack ProfileViewCallBack;
    private IncreaseCreditContract.ViewCallBack IncreaseCreditViewCallBack;
    private FavoriteExpertContract.ViewCallBack FavoriteExpertViewCallBack;
    private FavoriteLocationContract.ViewCallBack FavoriteLocationViewCallBack;
    private EditFavoriteLocationContract.ViewCallBack EditFavoriteLocationViewCallBack;


    public MvpModule(EnterMobileContract.ViewCallBack viewCallBack) {
        this.enterMobileViewCallBack = viewCallBack;
    }

    public MvpModule(SplashContract.ViewCallBack viewCallBack) {
        this.splashViewCallBack = viewCallBack;
    }

    public MvpModule(UpdateRequiredContract.ViewCallBack viewCallBack) {
        this.UpdateRequiredViewCallBack = viewCallBack;
    }

    public MvpModule(TryToConnectContract.ViewCallBack viewCallBack) {
        this.TryToConnectViewCallBack = viewCallBack;
    }

    public MvpModule(NameRegisterContract.ViewCallBack viewCallBack) {
        this.NameRegisterViewCallBack = viewCallBack;
    }

    public MvpModule(ValidateMobileContract.ViewCallBack viewCallBack) {
        this.ValidateMobileViewCallBack = viewCallBack;
    }

    public MvpModule(ProfilePicGenderRegisterContract.ViewCallBack viewCallBack) {
        this.ProfilePicGenderRegisterViewCallBack = viewCallBack;
    }

    public MvpModule(HomeContract.ViewCallBack viewCallBack) {
        this.HomeViewCallBack = viewCallBack;
    }

    public MvpModule(InfiniteCategoryContract.ViewCallBack viewCallBack) {
        this.InfiniteCategoryViewCallBack = viewCallBack;
    }

    public MvpModule(SubCategoryContract.ViewCallBack viewCallBack) {
        this.SubCategoryViewCallBack = viewCallBack;
    }

    public MvpModule(OrderSpecificationContract.ViewCallBack viewCallBack) {
        this.OrderSpecificationViewCallBack = viewCallBack;
    }

    public MvpModule(QuestionsContract.ViewCallBack viewCallBack) {
        this.QuestionsViewCallBack = viewCallBack;
    }

    public MvpModule(GetOrderDateTimeContract.ViewCallBack viewCallBack) {
        this.GetOrderDateTimeViewCallBack = viewCallBack;
    }

    public MvpModule(GetDistrictContract.ViewCallBack viewCallBack) {
        this.GetDistrictViewCallBack = viewCallBack;
    }

    public MvpModule(SuggestionsContract.ViewCallBack viewCallBack) {
        this.SuggestionsViewCallBack = viewCallBack;
    }

    public MvpModule(SuggestionDetailContract.ViewCallBack viewCallBack) {
        this.SuggestionDetailViewCallBack = viewCallBack;
    }

    public MvpModule(GetDetailedAddressContract.ViewCallBack viewCallBack) {
        this.GetDetailedAddressViewCallBack = viewCallBack;
    }

    public MvpModule(OrdersManagementContract.ViewCallBack viewCallBack) {
        this.OrdersManagementViewCallBack = viewCallBack;
    }

    public MvpModule(HangingStateContract.ViewCallBack viewCallBack) {
        this.HangingStateViewCallBack = viewCallBack;
    }

    public MvpModule(WaitingForStartStateContract.ViewCallBack viewCallBack) {
        this.WaitingForStartStateViewCallBack = viewCallBack;
    }

    public MvpModule(CanceledStateContract.ViewCallBack viewCallBack) {
        this.CanceledStateViewCallBack = viewCallBack;
    }

    public MvpModule(DoingStateContract.ViewCallBack viewCallBack) {
        this.DoingStateViewCallBack = viewCallBack;
    }

    public MvpModule(CompletedStateContract.ViewCallBack viewCallBack) {
        this.CompletedStateViewCallBack = viewCallBack;
    }

    public MvpModule(VerifyFinishOrderContract.ViewCallBack viewCallBack) {
        this.VerifyFinishOrderViewCallBack = viewCallBack;
    }

    public MvpModule(MapsContract.ViewCallBack viewCallBack) {
        this.MapsViewCallBack = viewCallBack;
    }

    public MvpModule(CommentContract.ViewCallBack viewCallBack) {
        this.CommentViewCallBack = viewCallBack;
    }

    public MvpModule(SearchContract.ViewCallBack viewCallBack) {
        this.SearchViewCallBack = viewCallBack;
    }

    public MvpModule(ProfileContract.ViewCallBack viewCallBack) {
        this.ProfileViewCallBack = viewCallBack;
    }

    public MvpModule(IncreaseCreditContract.ViewCallBack viewCallBack) {
        this.IncreaseCreditViewCallBack = viewCallBack;
    }

    public MvpModule(FavoriteExpertContract.ViewCallBack viewCallBack) {
        this.FavoriteExpertViewCallBack = viewCallBack;
    }

    public MvpModule(FavoriteLocationContract.ViewCallBack viewCallBack) {
        this.FavoriteLocationViewCallBack = viewCallBack;
    }

    public MvpModule(EditFavoriteLocationContract.ViewCallBack viewCallBack) {
        this.EditFavoriteLocationViewCallBack = viewCallBack;
    }



    @Provides
    public EnterMobileContract.ViewCallBack provideEnterMobileView() {
        return enterMobileViewCallBack;
    }

    @Provides
    public SplashContract.ViewCallBack provideSplashView() {
        return splashViewCallBack;
    }

    @Provides
    public UpdateRequiredContract.ViewCallBack provideUpdateRequiredView() {
        return UpdateRequiredViewCallBack;
    }

    @Provides
    public TryToConnectContract.ViewCallBack provideTryToConnectView() {
        return TryToConnectViewCallBack;
    }

    @Provides
    public NameRegisterContract.ViewCallBack provideNameRegisterView() {
        return NameRegisterViewCallBack;
    }

    @Provides
    public ValidateMobileContract.ViewCallBack provideValidateMobileView() {
        return ValidateMobileViewCallBack;
    }

    @Provides
    public ProfilePicGenderRegisterContract.ViewCallBack provideProfilePicGenderRegisterView() {
        return ProfilePicGenderRegisterViewCallBack;
    }

    @Provides
    public HomeContract.ViewCallBack provideHomeView() {
        return HomeViewCallBack;
    }

    @Provides
    public InfiniteCategoryContract.ViewCallBack provideInfiniteCategoryView() {
        return InfiniteCategoryViewCallBack;
    }

    @Provides
    public SubCategoryContract.ViewCallBack provideSubCategoryView() {
        return SubCategoryViewCallBack;
    }

    @Provides
    public OrderSpecificationContract.ViewCallBack provideOrderSpecificationView() {
        return OrderSpecificationViewCallBack;
    }

    @Provides
    public QuestionsContract.ViewCallBack provideQuestionsView() {
        return QuestionsViewCallBack;
    }

    @Provides
    public GetOrderDateTimeContract.ViewCallBack provideGetOrderDateTimeView() {
        return GetOrderDateTimeViewCallBack;
    }

    @Provides
    public GetDistrictContract.ViewCallBack provideGetDistrictView() {
        return GetDistrictViewCallBack;
    }

    @Provides
    public SuggestionsContract.ViewCallBack provideSuggestionsView() {
        return SuggestionsViewCallBack;
    }

    @Provides
    public SuggestionDetailContract.ViewCallBack provideSuggestionDetailView() {
        return SuggestionDetailViewCallBack;
    }

    @Provides
    public GetDetailedAddressContract.ViewCallBack provideGetDetailedAddressView() {
        return GetDetailedAddressViewCallBack;
    }

    @Provides
    public OrdersManagementContract.ViewCallBack provideOrdersManagementView() {
        return OrdersManagementViewCallBack;
    }

    @Provides
    public HangingStateContract.ViewCallBack provideHangingStateView() {
        return HangingStateViewCallBack;
    }

    @Provides
    public WaitingForStartStateContract.ViewCallBack provideWaitingForStartStateView() {
        return WaitingForStartStateViewCallBack;
    }

    @Provides
    public CanceledStateContract.ViewCallBack provideCanceledStateView() {
        return CanceledStateViewCallBack;
    }

    @Provides
    public DoingStateContract.ViewCallBack provideDoingStateView() {
        return DoingStateViewCallBack;
    }

    @Provides
    public CompletedStateContract.ViewCallBack provideCompletedStateView() {
        return CompletedStateViewCallBack;
    }

    @Provides
    public VerifyFinishOrderContract.ViewCallBack provideVerifyFinishOrderView() {
        return VerifyFinishOrderViewCallBack;
    }

    @Provides
    public MapsContract.ViewCallBack provideMapsView() {
        return MapsViewCallBack;
    }

    @Provides
    public CommentContract.ViewCallBack provideCommentView() {
        return CommentViewCallBack;
    }

    @Provides
    public SearchContract.ViewCallBack provideSearchView() {
        return SearchViewCallBack;
    }

    @Provides
    public ProfileContract.ViewCallBack provideProfileView() {
        return ProfileViewCallBack;
    }

    @Provides
    public IncreaseCreditContract.ViewCallBack provideIncreaseCreditView() {
        return IncreaseCreditViewCallBack;
    }

    @Provides
    public FavoriteExpertContract.ViewCallBack provideFavoriteExpertView() {
        return FavoriteExpertViewCallBack;
    }

    @Provides
    public FavoriteLocationContract.ViewCallBack provideFavoriteLocationView() {
        return FavoriteLocationViewCallBack;
    }

    @Provides
    public EditFavoriteLocationContract.ViewCallBack provideEditFavoriteLocationView() {
        return EditFavoriteLocationViewCallBack;
    }




    @Provides
    public EnterMobileContract.PresenterCallBack provideEnterMobilePresenter(EnterMobileContract.ViewCallBack view, EnterMobileModel enterMobileModel) {
        return new EnterMobilePresenter(view, enterMobileModel);
    }

    @Provides
    public SplashContract.PresenterCallBack provideSplashPresenter(SplashContract.ViewCallBack view, SplashModel splashModel) {
        return new SplashPresenter(view, splashModel);
    }

    @Provides
    public UpdateRequiredContract.PresenterCallBack provideUpdateRequiredPresenter(UpdateRequiredContract.ViewCallBack view, UpdateRequiredModel updateRequiredModel) {
        return new UpdateRequiredPresenter(view, updateRequiredModel);
    }

    @Provides
    public TryToConnectContract.PresenterCallBack provideTryToConnectPresenter(TryToConnectContract.ViewCallBack view, TryToConnectModel tryToConnectModel) {
        return new TryToConnectPresenter(view, tryToConnectModel);
    }

    @Provides
    public NameRegisterContract.PresenterCallBack provideNameRegisterPresenter(NameRegisterContract.ViewCallBack view, NameRegisterModel nameRegisterModel) {
        return new NameRegisterPresenter(view, nameRegisterModel);
    }

    @Provides
    public ValidateMobileContract.PresenterCallBack provideValidateMobilePresenter(ValidateMobileContract.ViewCallBack view, ValidateMobileModel validateMobileModel) {
        return new ValidateMobilePresenter(view, validateMobileModel);
    }

    @Provides
    public ProfilePicGenderRegisterContract.PresenterCallBack provideProfilePicGenderRegisterPresenter(ProfilePicGenderRegisterContract.ViewCallBack view, ProfilePicGenderRegisterModel profilePicGenderRegisterModel) {
        return new ProfilePicGenderRegisterPresenter(view, profilePicGenderRegisterModel);
    }

    @Provides
    public HomeContract.PresenterCallBack provideHomePresenter(HomeContract.ViewCallBack view, HomeModel homeModel) {
        return new HomePresenter(view, homeModel);
    }

    @Provides
    public InfiniteCategoryContract.PresenterCallBack provideInfiniteCategoryPresenter(InfiniteCategoryContract.ViewCallBack view, InfiniteCategoryModel infiniteCategoryModel) {
        return new InfiniteCategoryPresenter(view, infiniteCategoryModel);
    }

    @Provides
    public SubCategoryContract.PresenterCallBack provideSubCategoryPresenter(SubCategoryContract.ViewCallBack view, SubCategoryModel subCategoryModel) {
        return new SubCategoryPresenter(view, subCategoryModel);
    }

    @Provides
    public OrderSpecificationContract.PresenterCallBack provideOrderSpecificationPresenter(OrderSpecificationContract.ViewCallBack view, OrderSpecificationModel orderSpecificationModel) {
        return new OrderSpecificationPresenter(view, orderSpecificationModel);
    }

    @Provides
    public QuestionsContract.PresenterCallBack provideQuestionsPresenter(QuestionsContract.ViewCallBack view, QuestionsModel questionsModel) {
        return new QuestionsPresenter(view, questionsModel);
    }

    @Provides
    public GetOrderDateTimeContract.PresenterCallBack provideGetOrderDateTimePresenter(GetOrderDateTimeContract.ViewCallBack view, GetOrderDateTimeModel getOrderDateTimeModel) {
        return new GetOrderDateTimePresenter(view, getOrderDateTimeModel);
    }

    @Provides
    public GetDistrictContract.PresenterCallBack provideGetDistrictPresenter(GetDistrictContract.ViewCallBack view, GetDistrictModel getDistrictModel) {
        return new GetDistrictPresenter(view, getDistrictModel);
    }

    @Provides
    public SuggestionsContract.PresenterCallBack provideSuggestionsPresenter(SuggestionsContract.ViewCallBack view, SuggestionsModel suggestionsModel) {
        return new SuggestionsPresenter(view, suggestionsModel);
    }

    @Provides
    public SuggestionDetailContract.PresenterCallBack provideSuggestionDetailPresenter(SuggestionDetailContract.ViewCallBack view, SuggestionDetailModel suggestionDetailModel) {
        return new SuggestionDetailPresenter(view, suggestionDetailModel);
    }

    @Provides
    public GetDetailedAddressContract.PresenterCallBack provideGetDetailedAddressPresenter(GetDetailedAddressContract.ViewCallBack view, GetDetailedAddressModel getDetailedAddressModel) {
        return new GetDetailedAddressPresenter(view, getDetailedAddressModel);
    }

    @Provides
    public OrdersManagementContract.PresenterCallBack provideOrdersManagementPresenter(OrdersManagementContract.ViewCallBack view, OrdersManagementModel ordersManagementModel) {
        return new OrdersManagementPresenter(view, ordersManagementModel);
    }

    @Provides
    public HangingStateContract.PresenterCallBack provideHangingStatePresenter(HangingStateContract.ViewCallBack view, HangingStateModel hangingStateModel) {
        return new HangingStatePresenter(view, hangingStateModel);
    }

    @Provides
    public WaitingForStartStateContract.PresenterCallBack provideWaitingForStartStatePresenter(WaitingForStartStateContract.ViewCallBack view, WaitingForStartStateModel waitingForStartStateModel) {
        return new WaitingForStartStatePresenter(view, waitingForStartStateModel);
    }

    @Provides
    public CanceledStateContract.PresenterCallBack provideCanceledStatePresenter(CanceledStateContract.ViewCallBack view, CanceledStateModel canceledStateModel) {
        return new CanceledStatePresenter(view, canceledStateModel);
    }

    @Provides
    public DoingStateContract.PresenterCallBack provideDoingStatePresenter(DoingStateContract.ViewCallBack view, DoingStateModel doingStateModel) {
        return new DoingStatePresenter(view, doingStateModel);
    }

    @Provides
    public CompletedStateContract.PresenterCallBack provideCompletedStatePresenter(CompletedStateContract.ViewCallBack view, CompletedStateModel completedStateModel) {
        return new CompletedStatePresenter(view, completedStateModel);
    }

    @Provides
    public VerifyFinishOrderContract.PresenterCallBack provideVerifyFinishOrderPresenter(VerifyFinishOrderContract.ViewCallBack view, VerifyFinishOrderModel verifyFinishOrderModel) {
        return new VerifyFinishOrderPresenter(view, verifyFinishOrderModel);
    }

    @Provides
    public MapsContract.PresenterCallBack provideMapsPresenter(MapsContract.ViewCallBack view, MapsModel mapsModel) {
        return new MapsPresenter(view, mapsModel);
    }

    @Provides
    public CommentContract.PresenterCallBack provideCommentPresenter(CommentContract.ViewCallBack view, CommentModel commentModel) {
        return new CommentPresenter(view, commentModel);
    }

    @Provides
    public SearchContract.PresenterCallBack provideSearchPresenter(SearchContract.ViewCallBack view, SearchModel searchModel) {
        return new SearchPresenter(view, searchModel);
    }

    @Provides
    public ProfileContract.PresenterCallBack provideProfilePresenter(ProfileContract.ViewCallBack view, ProfileModel profileModel) {
        return new ProfilePresenter(view, profileModel);
    }

    @Provides
    public IncreaseCreditContract.PresenterCallBack provideIncreaseCreditPresenter(IncreaseCreditContract.ViewCallBack view, IncreaseCreditModel increaseCreditModel) {
        return new IncreaseCreditPresenter(view, increaseCreditModel);
    }

    @Provides
    public FavoriteExpertContract.PresenterCallBack provideFavoriteExpertPresenter(FavoriteExpertContract.ViewCallBack view, FavoriteExpertModel favoriteExpertModel) {
        return new FavoriteExpertPresenter(view, favoriteExpertModel);
    }

    @Provides
    public FavoriteLocationContract.PresenterCallBack provideFavoriteLocationPresenter(FavoriteLocationContract.ViewCallBack view, FavoriteLocationModel favoriteLocationModel) {
        return new FavoriteLocationPresenter(view, favoriteLocationModel);
    }

    @Provides
    public EditFavoriteLocationContract.PresenterCallBack provideEditFavoriteLocationPresenter(EditFavoriteLocationContract.ViewCallBack view, EditFavoriteLocationModel editFavoriteLocationModel) {
        return new EditFavoriteLocationPresenter(view, editFavoriteLocationModel);
    }

}