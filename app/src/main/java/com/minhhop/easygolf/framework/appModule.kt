package com.minhhop.easygolf.framework

import com.minhhop.core.data.repository.*
import com.minhhop.core.usercase.contact.GetContactFromPhone
import com.minhhop.core.usercase.contact.SyncContact
import com.minhhop.core.usercase.country.*
import com.minhhop.core.usercase.file.*
import com.minhhop.core.usercase.firebase.*
import com.minhhop.core.usercase.firebase.FetchDataScoreGolfMembersInBattleFirebase
import com.minhhop.core.usercase.firebase.GetScoreUserInBattleFirebase
import com.minhhop.core.usercase.firebase.PostScoreInBattleToFirebase
import com.minhhop.core.usercase.general.*
import com.minhhop.core.usercase.golf.*
import com.minhhop.core.usercase.user.*
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import com.minhhop.easygolf.framework.database.FirebaseManager1
import com.minhhop.easygolf.framework.database.PreferenceManager
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.implementation.*
import com.minhhop.easygolf.framework.network.ConnectActivity
import com.minhhop.easygolf.framework.network.remote.GeneralService
import com.minhhop.easygolf.framework.network.remote.GolfService
import com.minhhop.easygolf.framework.network.remote.UserService
import com.minhhop.easygolf.framework.network.retrofit.ProviderRetrofit
import com.minhhop.easygolf.presentation.account.AccountSettingsViewModel
import com.minhhop.easygolf.presentation.account.AccountUpdateViewModel
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailViewModel
import com.minhhop.easygolf.presentation.club.detail.club.FollowersViewModel
import com.minhhop.easygolf.presentation.club.list.ClubHomeViewModel
import com.minhhop.easygolf.presentation.country.CountryViewModel
import com.minhhop.easygolf.presentation.course.ScoreCardCourseViewModel
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import com.minhhop.easygolf.presentation.feed.CreateNewPostViewModel
import com.minhhop.easygolf.presentation.feed.SearchCoursesViewModel
import com.minhhop.easygolf.presentation.feed.SelectFriendViewModel
import com.minhhop.easygolf.presentation.feedback.FeedbackViewModel
import com.minhhop.easygolf.presentation.forgotpassword.ForgotPasswordViewModel
import com.minhhop.easygolf.presentation.gallery.EasyGolfGalleryViewModel
import com.minhhop.easygolf.presentation.golf.play.PlayGolfViewModel
import com.minhhop.easygolf.presentation.group.chat.ChatViewModel
import com.minhhop.easygolf.presentation.home.EasyGolfHomeViewModel
import com.minhhop.easygolf.presentation.home.chat.ChatHomeViewModel
import com.minhhop.easygolf.presentation.home.newsfeed.NewPostViewModel
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedViewModel
import com.minhhop.easygolf.presentation.home.newsfeed.PostDetailsViewModel
import com.minhhop.easygolf.presentation.like.LikesViewModel
import com.minhhop.easygolf.presentation.more.MoreViewModel
import com.minhhop.easygolf.presentation.notification.NotificationRepository
import com.minhhop.easygolf.presentation.notification.NotificationViewModel
import com.minhhop.easygolf.presentation.pass_score.PassScoreViewModel
import com.minhhop.easygolf.presentation.password.ChangePasswordViewModel
import com.minhhop.easygolf.presentation.player.add.AddPlayerViewModel
import com.minhhop.easygolf.presentation.policy.PolicyTermsViewModel
import com.minhhop.easygolf.presentation.requestcourse.RequestCourseViewModel
import com.minhhop.easygolf.presentation.round.RoundHistoryRespository
import com.minhhop.easygolf.presentation.round.RoundHistoryViewModel
import com.minhhop.easygolf.presentation.rules.GolfRulesRepository
import com.minhhop.easygolf.presentation.rules.GolfRulesViewModel
import com.minhhop.easygolf.presentation.signin.SignInViewModel
import com.minhhop.easygolf.presentation.signup.RegisterViewModel
import com.minhhop.easygolf.presentation.signup.SignUpViewModel
import com.minhhop.easygolf.presentation.splash.SplashViewModel
import com.minhhop.easygolf.presentation.user.UserProfileRepository
import com.minhhop.easygolf.presentation.user.UserProfileViewModel
import com.minhhop.easygolf.presentation.verify_code.VerifyCodeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        ConnectActivity(context = androidContext())
    }

    single {
        RealmManager()
    }

    single {
        FirebaseManager1()
    }

    single {
        PreferenceManager(context = androidContext())
    }

    single {
        ProviderRetrofit(preferenceManager = get(), connectActivity = get())
    }

    single {
        createNetworkService<GeneralService>(providerRetrofit = get())
    }

    single {
        createNetworkService<UserService>(providerRetrofit = get())
    }

    single {
        createNetworkService<GolfService>(providerRetrofit = get())
    }


    single {
        CountryRepository(countryDataSource = CountryImpl(generalService = get(), realmManager = get(), connectActivity = get()))
    }

    single {
        ContactRepository(contactDataSource = ContactImpl(context = androidContext(), generalService = get()))
    }

    single {
        GeneralRepository(generalDataSource = GeneralImpl(generalService = get(),realmManager = get()))
    }

    single {
        UserRepository(userDataSource = UserImpl(preferenceManager = get(), realmManager = get(), userService = get()))
    }

    single {
        GolfRepository(golfDataSource = GolfImpl(golfService = get(), generalService = get(), realmManager = get()))
    }

    single {
        FileHelperRepository(fileHelperDataSource = FileHelperImpl())
    }

    single {
        FirebaseRepository(firebaseDataSource = FirebaseImpl(realmManager = get()))
    }

    single {
        Interactors(
                /**
                 * MARK: country
                 * */
                getCountries = GetCountries(countryRepository = get()),
                getsCountriesInLocal = GetsCountriesInLocal(countryRepository = get()),
                isCountriesExitInLocal = IsCountriesExitInLocal(countryRepository = get()),
                saveCountries = SaveCountries(countryRepository = get()),
                getDefaultCountry = GetDefaultCountry(countryRepository = get()),
                /**
                 * MARK: contact
                 * */
                syncContact = SyncContact(contactRepository = get()),
                getContactFromPhone = GetContactFromPhone(contactRepository = get()),
                /**
                 * MARK: general
                 * */
                fetchPolicyTerm = FetchPolicyTerm(generalRepository = get()),
                fetchCountries = FetchCountries(generalRepository = get()),
                searchCountriesInLocal = SearchCountriesInLocal(generalRepository = get()),
                getDefaultCountryByLocale = GetDefaultCountryByLocale(generalRepository = get()),
                getEasyGolfAppDataInLocal = GetEasyGolfAppDataInLocal(generalRepository = get()),
                updateEasyGolfAppDataInLocal = UpdateEasyGolfAppDataInLocal(generalRepository = get()),
                deleteEasyGolfAppDataByRoundId = DeleteEasyGolfAppDataByRoundId(generalRepository = get()),
                /**
                 * MARK: user
                 * */
                signIn = SignIn(userRepository = get()),
                signUp = SignUp(userRepository = get()),
                logout = Logout(userRepository = get()),
                getRules = GetGolfRulesFromLocal(userRepository = get()),
                signInWithFacebook = SignInWithFacebook(userRepository = get()),
                getProfileInLocal = GetProfileInLocal(userRepository = get()),
                updateUserProfileInLocal = UpdateUserProfileInLocal(userRepository = get()),
                getProfileUser = GetProfileUser(userRepository = get()),
                isReadySignIn = IsReadySignIn(userRepository = get()),
                saveAccessTokenInLocal = SaveAccessTokenInLocal(userRepository = get()),
                verifyCodeByToken = VerifyCodeByToken(userRepository = get()),
                resendCodeOTP = ResendCodeOTP(userRepository = get()),
                fetchFriends = FetchFriends(userRepository = get()),
                userForgotPassword = UserForgotPassword(userRepository = get()),
                unregisterForLoginBySocial = UnregisterForLoginBySocial(userRepository = get()),
                updateProfileUser = UpdateProfileUser(userRepository = get()),
                /**
                 * MARK: golf
                 * */
                startRoundGolf = StartRoundGolf(golfRepository = get()),
                pushNotificationToMemberInBattle = PushNotificationToMemberInBattle(golfRepository = get()),
                fetchRoundGolf = FetchRoundGolf(golfRepository = get()),
                clubsNearby = ClubsNearby(golfRepository = get()),
                fetchSuggestedClubs = FetchSuggestedClubs(golfRepository = get()),
                fetchRecommendClub = FetchRecommendClub(golfRepository = get()),
                getClubDetail = GetClubDetail(golfRepository = get()),
                newsfeeds = NewsFeedsRepository(golfRepository = get()),
                userProfileRepository = UserProfileRepository(golfRepository = get()),
                newPost = NewPostRepository(golfRepository = get()),
                roundHistoryRespository = RoundHistoryRespository(golfRepository = get()),
                golfRulesRepository = GolfRulesRepository(golfRepository = get()),
                postDetails = PostDetailsRepository(golfRepository = get()),
                notificationRepository = NotificationRepository(golfRepository = get()),
                fetchClubPhotos = FetchClubPhotos(golfRepository = get()),
                fetchClubReview = FetchClubReview(golfRepository = get()),
                addReviewForClub = AddReviewForClub(golfRepository = get()),
                deletePhotoFromCurrentClub = DeletePhotoFromCurrentClub(golfRepository = get()),
                uploadPhotoForClub = UploadPhotoForClub(golfRepository = get()),
                startExploreCourse = StartExploreCourse(golfRepository = get()),
                getRoundGolfByCourseInLocal = GetRoundGolfByCourseInLocal(golfRepository = get()),
                getRoundGolfByIdInLocal = GetRoundGolfByIdInLocal(golfRepository = get()),
                insertOrUpdateRoundGolfInLocal = InsertOrUpdateRoundGolfInLocal(golfRepository = get()),
                deleteRoundGolfInLocal = DeleteRoundGolfInLocal(golfRepository = get()),
                deleteAllDataScoreGolfOfRoundInLocal = DeleteAllDataScoreGolfOfRoundInLocal(golfRepository = get()),
                insertOrUpdateScoreHole = InsertOrUpdateScoreHole(golfRepository = get()),
                getAllDataScoreGolfByRoundInLocal = GetAllDataScoreGolfByRoundInLocal(golfRepository = get()),
                getScoreAtHole = GetScoreAtHole(golfRepository = get()),
                sendFeedbackGolf = SendFeedbackGolf(golfRepository = get()),
                passScoreGolfForCourse = PassScoreGolfForCourse(golfRepository = get()),
                uploadPhotoScorecardGolf = UploadPhotoScorecardGolf(golfRepository = get()),
                onCompleteRoundGolf = OnCompleteRoundGolf(golfRepository = get()),
                onFollowForClub = OnFollowForClub(golfRepository = get()),
                getRoundGolfExitInLocal = GetRoundGolfExitInLocal(golfRepository = get()),
                friendApproveRoundComplete = FriendApproveRoundComplete(golfRepository = get()),
                friendNotAcceptRoundComplete = FriendNotAcceptRoundComplete(golfRepository = get()),
                /**
                 * MARK: file helper
                 * */
                createImageFile = CreateImageFile(fileHelperRepository = get()),
                createImageFileWithInputStream = CreateImageFileWithInputStream(fileHelperRepository = get()),
                deleteFileInLocal = DeleteFileInLocal(fileHelperRepository = get()),
                resizeImageInLocal = ResizeImageInLocal(fileHelperRepository = get()),
                writeBytesToFile = WriteBytesToFile(fileHelperRepository = get()),
                /**
                 * MARK: firebase
                 * */
                getBattleRoundFindFirstInFirebaseForCurrentUser = GetBattleRoundFindFirstInFirebaseForCurrentUser(firebaseRepository = get()),
                getBattleRoundInFirebase = GetBattleRoundInFirebase(firebaseRepository = get()),
                removeBattleRoundInFirebase = RemoveBattleRoundInFirebase(firebaseRepository = get()),
                getBattleRoundWithCourseInFirebase = GetBattleRoundWithCourseInFirebase(firebaseRepository = get()),
                getProfileUserFromFirebase = GetProfileUserFromFirebase(firebaseRepository = get()),
                fetchMembersWithIdInBattleFirebase = FetchMembersWithIdInBattleFirebase(firebaseRepository = get()),
                fetchMemberInBattleFirebase = FetchMemberInBattleFirebase(firebaseRepository = get()),
                fetchDataScoreGolfMembersInBattleFirebase = FetchDataScoreGolfMembersInBattleFirebase(firebaseRepository = get()),
                addMembersToBattleInFirebase = AddMembersToBattleInFirebase(firebaseRepository = get()),
                removeMemberFromBattleInFirebase = RemoveMemberFromBattleInFirebase(firebaseRepository = get()),
                postScoreInBattleToFirebase = PostScoreInBattleToFirebase(firebaseRepository = get()),
                getDataScoreAtRoundInFirebase = GetDataScoreAtRoundInFirebase(firebaseRepository = get()),
                getScoreUserInBattleFirebase = GetScoreUserInBattleFirebase(firebaseRepository = get()),
                createBattleGameInFirebase = CreateBattleGameInFirebase(firebaseRepository = get()),
                updateHandicapUserInFirebase = UpdateHandicapUserInFirebase(firebaseRepository = get()),
                updateStatusPendingInBattleForUser = UpdateStatusPendingInBattleForUser(firebaseRepository = get()),
                changeTeeTypeInTheBattle = ChangeTeeTypeInTheBattle(firebaseRepository = get()),
                updateMathForRoundGolfInLocal = UpdateMathForRoundGolfInLocal(golfRepository = get()),
                changeTeeTypeForRoundGolfInLocal = ChangeTeeTypeForRoundGolfInLocal(golfRepository = get()),
                isRoundExitInFirebase = IsRoundExitInFirebase(firebaseRepository = get())
        )
    }

    viewModel {
        GolfRulesViewModel(interactors = get())
    }
    viewModel {
        FollowersViewModel(interactors = get())
    }
    viewModel {
        SearchCoursesViewModel(interactors = get())
    }

    viewModel {
        RegisterViewModel(interactors = get())
    }

    viewModel {
        VerifyCodeViewModel(interactors = get())
    }

    viewModel {
        ChatViewModel(interactors = get(), firebaseManager1 = get())
    }

    viewModel {
        MoreViewModel(interactors = get())
    }

    viewModel {
        PlayGolfViewModel(interactors = get())
    }
    viewModel {
        UserProfileViewModel(interactors = get())
    }

    viewModel {
        SelectFriendViewModel(interactors = get())
    }

    /**
     * Start update
     * */
    viewModel {
        SplashViewModel(interactors = get())
    }
    viewModel {
        SignInViewModel(interactors = get())
    }
    viewModel {
        SignUpViewModel(interactors = get())
    }

    viewModel {
        PolicyTermsViewModel(interactors = get())
    }

    viewModel {
        CreateNewPostViewModel(interactors = get())
    }
    viewModel {
        EasyGolfHomeViewModel(interactors = get())
    }

    viewModel {
        NewsFeedViewModel(interactors = get())
    }

    viewModel {
        PostDetailsViewModel(interactors = get())
    }

    viewModel {
        NewPostViewModel(interactors = get())
    }

    viewModel {
        GolfClubViewModel(interactors = get())
    }

    viewModel {
        ClubHomeViewModel(interactors = get())
    }
    viewModel {
        NotificationViewModel(interactors = get())
    }

    viewModel {
        ChatHomeViewModel(interactors = get())
    }

    viewModel {
        RoundHistoryViewModel(interactors = get())
    }

    viewModel {
        EasyGolfClubDetailViewModel(interactors = get())
    }

    viewModel {
        AddPlayerViewModel(interactors = get())
    }

    viewModel {
        EasyGolfGalleryViewModel(interactors = get())
    }
    viewModel {
        ScoreCardCourseViewModel()
    }
    viewModel {
        FeedbackViewModel(interactors = get())
    }

    viewModel {
        PassScoreViewModel(interactors = get())
    }

    viewModel {
        EndGameViewModel(interactors = get())
    }
    viewModel {
        ForgotPasswordViewModel(interactors = get())
    }
    viewModel {
        CountryViewModel(interactors = get())
    }
    viewModel {
        LikesViewModel(interactors = get())
    }
    viewModel {
        AccountSettingsViewModel(interactors = get())
    }
    viewModel {
        AccountUpdateViewModel(interactors = get())
    }
    viewModel {
        ChangePasswordViewModel(interactors = get())
    }
    viewModel {
        RequestCourseViewModel(interactors = get())
    }
}

inline fun <reified T> createNetworkService(providerRetrofit: ProviderRetrofit) = providerRetrofit.createNetworkService<T>()