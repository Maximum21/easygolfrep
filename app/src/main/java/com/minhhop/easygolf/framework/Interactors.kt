package com.minhhop.easygolf.framework

import com.minhhop.core.data.repository.NewPostRepository
import com.minhhop.core.data.repository.NewsFeedsRepository
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
import com.minhhop.easygolf.presentation.notification.NotificationRepository
import com.minhhop.easygolf.presentation.round.RoundHistoryRespository
import com.minhhop.easygolf.presentation.rules.GolfRulesRepository
import com.minhhop.easygolf.presentation.user.UserProfileRepository

data class Interactors (
        /**
         * MARK: country
         * */
        val getCountries: GetCountries,
        val getsCountriesInLocal: GetsCountriesInLocal,
        val isCountriesExitInLocal: IsCountriesExitInLocal,
        val saveCountries: SaveCountries,
        val getDefaultCountry: GetDefaultCountry,
        /**
         * MARK: contact
         * */
        val syncContact:SyncContact,
        val getContactFromPhone: GetContactFromPhone,
        /**
         * MARK: general
         * */
        val fetchPolicyTerm: FetchPolicyTerm,
        val fetchCountries: FetchCountries,
        val searchCountriesInLocal: SearchCountriesInLocal,
        val getDefaultCountryByLocale: GetDefaultCountryByLocale,
        val getEasyGolfAppDataInLocal: GetEasyGolfAppDataInLocal,
        val updateEasyGolfAppDataInLocal: UpdateEasyGolfAppDataInLocal,
        val deleteEasyGolfAppDataByRoundId: DeleteEasyGolfAppDataByRoundId,
        /**
         * MARK: user
         * */
        val signIn: SignIn,
        val signUp: SignUp,
        val logout: Logout,
        val signInWithFacebook: SignInWithFacebook,
        val getProfileInLocal: GetProfileInLocal,
        val getRules: GetGolfRulesFromLocal,
        val getProfileUser: GetProfileUser,
        val isReadySignIn: IsReadySignIn,
        val saveAccessTokenInLocal: SaveAccessTokenInLocal,
        val verifyCodeByToken: VerifyCodeByToken,
        val resendCodeOTP: ResendCodeOTP,
        val fetchFriends: FetchFriends,
        val userForgotPassword: UserForgotPassword,
        val unregisterForLoginBySocial: UnregisterForLoginBySocial,
        val updateUserProfileInLocal: UpdateUserProfileInLocal,
        val updateProfileUser: UpdateProfileUser,
        /**
         * MARK: golf
         * */
        val startRoundGolf: StartRoundGolf,
        val pushNotificationToMemberInBattle: PushNotificationToMemberInBattle,
        val fetchRoundGolf: FetchRoundGolf,
        val clubsNearby: ClubsNearby,
        val fetchSuggestedClubs: FetchSuggestedClubs,
        val fetchRecommendClub: FetchRecommendClub,
        val getClubDetail: GetClubDetail,
        val newsfeeds: NewsFeedsRepository,
        val roundHistoryRespository: RoundHistoryRespository,
        val golfRulesRepository: GolfRulesRepository,
        val userProfileRepository: UserProfileRepository,
        val newPost: NewPostRepository,
        val notificationRepository: NotificationRepository,
        val postDetails: PostDetailsRepository,
        val fetchClubPhotos: FetchClubPhotos,
        val fetchClubReview: FetchClubReview,
        val addReviewForClub: AddReviewForClub,
        val deletePhotoFromCurrentClub: DeletePhotoFromCurrentClub,
        val uploadPhotoForClub: UploadPhotoForClub,
        val startExploreCourse: StartExploreCourse,
        val getRoundGolfByCourseInLocal: GetRoundGolfByCourseInLocal,
        val getRoundGolfByIdInLocal: GetRoundGolfByIdInLocal,
        val insertOrUpdateRoundGolfInLocal: InsertOrUpdateRoundGolfInLocal,
        val deleteRoundGolfInLocal: DeleteRoundGolfInLocal,
        val deleteAllDataScoreGolfOfRoundInLocal: DeleteAllDataScoreGolfOfRoundInLocal,
        val insertOrUpdateScoreHole: InsertOrUpdateScoreHole,
        val getAllDataScoreGolfByRoundInLocal: GetAllDataScoreGolfByRoundInLocal,
        val getScoreAtHole: GetScoreAtHole,
        val sendFeedbackGolf: SendFeedbackGolf,
        val passScoreGolfForCourse: PassScoreGolfForCourse,
        val uploadPhotoScorecardGolf: UploadPhotoScorecardGolf,
        val onCompleteRoundGolf: OnCompleteRoundGolf,
        val onFollowForClub: OnFollowForClub,
        val updateMathForRoundGolfInLocal: UpdateMathForRoundGolfInLocal,
        val changeTeeTypeForRoundGolfInLocal: ChangeTeeTypeForRoundGolfInLocal,
        val getRoundGolfExitInLocal: GetRoundGolfExitInLocal,
        val friendApproveRoundComplete: FriendApproveRoundComplete,
        val friendNotAcceptRoundComplete: FriendNotAcceptRoundComplete,
        /**
         * MARK:  file helper
         * */
        val createImageFile: CreateImageFile,
        val createImageFileWithInputStream: CreateImageFileWithInputStream,
        val deleteFileInLocal: DeleteFileInLocal,
        val resizeImageInLocal: ResizeImageInLocal,
        val writeBytesToFile: WriteBytesToFile,
        /**
         * MARK: firebase
         * */
        val getBattleRoundFindFirstInFirebaseForCurrentUser: GetBattleRoundFindFirstInFirebaseForCurrentUser,
        val getBattleRoundInFirebase: GetBattleRoundInFirebase,
        val removeBattleRoundInFirebase: RemoveBattleRoundInFirebase,
        val getBattleRoundWithCourseInFirebase: GetBattleRoundWithCourseInFirebase,
        val getProfileUserFromFirebase: GetProfileUserFromFirebase,
        val fetchMembersWithIdInBattleFirebase: FetchMembersWithIdInBattleFirebase,
        val fetchMemberInBattleFirebase: FetchMemberInBattleFirebase,
        val fetchDataScoreGolfMembersInBattleFirebase: FetchDataScoreGolfMembersInBattleFirebase,
        val addMembersToBattleInFirebase: AddMembersToBattleInFirebase,
        val removeMemberFromBattleInFirebase: RemoveMemberFromBattleInFirebase,
        val postScoreInBattleToFirebase: PostScoreInBattleToFirebase,
        val getDataScoreAtRoundInFirebase: GetDataScoreAtRoundInFirebase,
        val getScoreUserInBattleFirebase: GetScoreUserInBattleFirebase,
        val createBattleGameInFirebase: CreateBattleGameInFirebase,
        val updateHandicapUserInFirebase: UpdateHandicapUserInFirebase,
        val updateStatusPendingInBattleForUser: UpdateStatusPendingInBattleForUser,
        val changeTeeTypeInTheBattle: ChangeTeeTypeInTheBattle,
        val isRoundExitInFirebase: IsRoundExitInFirebase
)