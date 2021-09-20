package com.minhhop.easygolf.framework.common

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.core.domain.firebase.NotificationModel
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.bundle.*
import com.minhhop.easygolf.framework.common.Contains.UPDATE_REQEUST_CODE
import com.minhhop.easygolf.presentation.account.AccountUpdateActivity
import com.minhhop.easygolf.presentation.club.detail.EasyGolfClubDetailActivity
import com.minhhop.easygolf.presentation.country.CountryActivity
import com.minhhop.easygolf.presentation.course.ScoreCardCourseActivity
import com.minhhop.easygolf.presentation.endgame.EndGameActivity
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import com.minhhop.easygolf.presentation.feedback.FeedbackActivity
import com.minhhop.easygolf.presentation.forgotpassword.ForgotPasswordActivity
import com.minhhop.easygolf.presentation.gallery.EasyGolfGalleryActivity
import com.minhhop.easygolf.presentation.golf.play.PlayGolfActivity
import com.minhhop.easygolf.presentation.home.EasyGolfHomeActivity
import com.minhhop.easygolf.presentation.pass_score.EnterPassScoreActivity
import com.minhhop.easygolf.presentation.player.add.AddPlayerActivity
import com.minhhop.easygolf.presentation.policy.PolicyTermsActivity
import com.minhhop.easygolf.presentation.round.RoundHistoryActivity
import com.minhhop.easygolf.presentation.scorecard.ScorecardActivity
import com.minhhop.easygolf.presentation.signin.SignInActivity
import com.minhhop.easygolf.presentation.signup.SignUpActivity
import com.minhhop.easygolf.presentation.user.UserProfileActivity
import com.minhhop.easygolf.presentation.userfeed.FeedsActivity
import com.minhhop.easygolf.presentation.verify_code.VerifyCodeActivity
import com.minhhop.easygolf.views.activities.PostDetailsActivity
import com.minhhop.easygolf.views.activities.SelectCourseActivity
import java.io.File

object EasyGolfNavigation {
    private fun startActivity(context: Context, cl: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(context, cl)
        bundle?.let {
            intent.putExtras(it)
        }
        context.startActivity(intent, bundle)
    }

    private fun startActivityForResult(activity: Activity, cl: Class<*>, requestCode: Int, bundle: Bundle? = null) {
        val intent = Intent(activity, cl)
        bundle?.let {
            intent.putExtras(it)
        }
        activity.startActivityForResult(intent, requestCode, bundle)
    }

    fun startUserDetailsActivity(activity: Activity, profileId: String) {
        activity.startActivity(Intent(activity, UserProfileActivity::class.java).putExtra("id", profileId))
    }

    private fun startActivityForResult(fragment: Fragment, cl: Class<*>, requestCode: Int, bundle: Bundle? = null) {
        val intent = Intent(fragment.context, cl)
        bundle?.let {
            intent.putExtras(it)
        }
        fragment.startActivityForResult(intent, requestCode)
    }

    fun checkSelfPermission(permission: String, requestCode: Int, activity: EasyGolfActivity<*>, explainSnack: String? = null, onGranted: () -> Unit) {
        checkSelfPermission(permission, requestCode, activity, onGranted) {
            activity.getViewRoot()?.let { viewRoot ->
                Snackbar.make(viewRoot, explainSnack ?: permission, Snackbar.LENGTH_INDEFINITE)
                        .setAction(activity.getString(android.R.string.ok)) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            activity.startActivity(intent)
                        }.show()
            }
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int, activity: EasyGolfActivity<*>, onGranted: () -> Unit, onDenied: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(activity,
                    permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                onGranted()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                onDenied()
            }
            else -> {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int, fragment: EasyGolfFragment<*>, explainSnack: String? = null, onGranted: () -> Unit) {
        fragment.context?.let { context ->
            checkSelfPermission(permission, requestCode, fragment, onGranted) {
                Snackbar.make(fragment.viewRoot, explainSnack ?: permission, Snackbar.LENGTH_LONG)
                        .setAction(context.getString(android.R.string.ok)) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            fragment.startActivity(intent)
                        }
                        .show()
            }
        }
    }

    fun checkSelfPermission(permission: String, requestCode: Int, fragment: EasyGolfFragment<*>, onGranted: () -> Unit, onDenied: () -> Unit) {
        fragment.context?.let { context ->
            when {
                ContextCompat.checkSelfPermission(context,
                        permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onGranted()
                }
                fragment.shouldShowRequestPermissionRationale(permission) -> {
                    onDenied()
                }
                else -> {
                    fragment.requestPermissions(arrayOf(permission), requestCode)
                }
            }
        }
    }

    fun signInDirection(activity: AppCompatActivity) {
        startActivity(activity, SignInActivity::class.java)
        activity.overridePendingTransition(0, 0)
    }

    fun signUpDirection(activity: AppCompatActivity) {
        startActivity(activity, SignUpActivity::class.java)
    }

    fun policyTermDirection(context: Context, option: PolicyTerm.Option) {
        val bundle = Bundle()
        bundle.putString("Option", option.value)
        startActivity(context, PolicyTermsActivity::class.java)
    }

    fun policyTermBundle(intent: Intent): String? = intent.getStringExtra("Option")

    fun easyGolfHomeDirection(context: Context, bundle: Bundle? = null) {
        startActivity(context, EasyGolfHomeActivity::class.java, bundle)
    }

    fun easyGolfCourseDetailDirection(context: Context, id: String, name: String?, photo: String?) {
        val bundle = Bundle()
        bundle.putString(CourseBundle::id.name, id)
        bundle.putString(CourseBundle::name.name, name)
        bundle.putString(CourseBundle::photo.name, photo)
        startActivity(context, EasyGolfClubDetailActivity::class.java, bundle)
    }

    fun easyGolfNewPostDirection(context: Context) {
        startActivity(context, CreatePostActivity::class.java)
    }

    fun easyGolfCourseDetailBundle(intent: Intent): CourseBundle? {
        return intent.getStringExtra(CourseBundle::id.name)?.let { id ->
            intent.getStringExtra(CourseBundle::name.name)?.let { name ->
                CourseBundle(id, name, intent.getStringExtra(CourseBundle::photo.name))
            }
        }
    }

    fun playGolfBundle(intent: Intent): PlayGolfBundle? {

        val typeGame = when (intent.getIntExtra(PlayGolfBundle::typeGame.name, PlayGolfBundle.TypeGame.EXPLORE.value)) {
            PlayGolfBundle.TypeGame.EXPLORE.value -> PlayGolfBundle.TypeGame.EXPLORE
            PlayGolfBundle.TypeGame.NEW_GAME.value -> PlayGolfBundle.TypeGame.NEW_GAME
            PlayGolfBundle.TypeGame.BATTLE_GAME.value -> PlayGolfBundle.TypeGame.BATTLE_GAME
            else -> PlayGolfBundle.TypeGame.EXPLORE
        }
        return intent.getStringExtra(PlayGolfBundle::teeType.name)?.let { teeType ->
            PlayGolfBundle(typeGame,
                    intent.getStringExtra(PlayGolfBundle::roundId.name),
                    intent.getStringExtra(PlayGolfBundle::courseId.name), teeType,
                    intent.getStringExtra(PlayGolfBundle::scorecards.name),
                    intent.getStringExtra(PlayGolfBundle::members.name))
        }
    }

    fun playGolfDirection(context: Context, playGolfBundle: PlayGolfBundle?) {
        val bundle = Bundle()
        playGolfBundle?.apply {
            bundle.putString(PlayGolfBundle::roundId.name, this.roundId)
            bundle.putString(PlayGolfBundle::courseId.name, this.courseId)
            bundle.putString(PlayGolfBundle::teeType.name, this.teeType)
            bundle.putInt(PlayGolfBundle::typeGame.name, this.typeGame.value)
            bundle.putString(PlayGolfBundle::scorecards.name, this.scorecards)
            bundle.putString(PlayGolfBundle::members.name, this.members)
        }
        startActivity(context, PlayGolfActivity::class.java, bundle)
    }

    private fun setBundleCourse(club: String): Bundle {
        val bundle = Bundle()
        bundle.putString(SearchCourseBundle::club.name, club)
        return bundle
    }

    private fun setBundlePost(tag: Int = -1, postId: String? = null, feed: String? = null): Bundle {
        val bundle = Bundle()
        bundle.putInt(PostDetailsBundle::tag.name, tag)
        bundle.putString(PostDetailsBundle::postId.name, postId)
        bundle.putString(PostDetailsBundle::newsFeed.name, feed)
        return bundle
    }

    private fun setBundleAddPlayer(limit: Int = -1, blackList: String? = null, title: String? = null): Bundle {
        val bundle = Bundle()
        bundle.putInt(AddPlayerBundle::limit.name, limit)
        bundle.putString(AddPlayerBundle::blackList.name, blackList)
        bundle.putString(AddPlayerBundle::title.name, title)
        return bundle
    }

    fun addPlayerDirection(activity: Activity, requestCode: Int, limit: Int = -1, blackList: String? = null, title: String? = null) {
        startActivityForResult(activity, AddPlayerActivity::class.java, requestCode, setBundleAddPlayer(limit, blackList, title))
    }

    fun addPostDirection(fragment: Fragment, requestCode: Int, tag: Int = -1, postID: String? = null, feed: String? = null, java: Class<PostDetailsActivity>) {
        startActivityForResult(fragment, java, requestCode, setBundlePost(tag, postID, feed))
    }

    fun addPostDirection(activity: Activity, requestCode: Int, tag: Int = -1, postID: String? = null, feed: String? = null, java: Class<PostDetailsActivity>) {
        startActivityForResult(activity, java, requestCode, setBundlePost(tag, postID, feed))
    }

    fun createPostDirection(activity: Activity, requestCode: Int, tag: Int = -1, postID: String? = null, feed: String? = null, java: Class<CreatePostActivity>) {
        startActivityForResult(activity, java, requestCode, setBundlePost(tag, postID, feed))
    }

    fun createPostDirection(fragment: Fragment, requestCode: Int, tag: Int = -1, postID: String? = null, feed: String? = null, java: Class<CreatePostActivity>) {
        startActivityForResult(fragment, java, requestCode, setBundlePost(tag, postID, feed))
    }

    fun addPlayerDirection(fragment: Fragment, requestCode: Int, limit: Int = -1, blackList: String? = null, title: String? = null) {
        startActivityForResult(fragment, AddPlayerActivity::class.java, requestCode, setBundleAddPlayer(limit, blackList, title))
    }

    fun addPlayerBundle(intent: Intent): AddPlayerBundle? {
        return AddPlayerBundle(
                intent.getIntExtra(AddPlayerBundle::limit.name, -1),
                intent.getStringExtra(AddPlayerBundle::result.name),
                intent.getStringExtra(AddPlayerBundle::blackList.name),
                intent.getStringExtra(AddPlayerBundle::title.name)
        )
    }

    fun SearchCourseBundle(intent: Intent): SearchCourseBundle? {
        return SearchCourseBundle(
                intent.getStringExtra(SearchCourseBundle::club.name)!!
        )
    }

    fun PostBundle(intent: Intent): PostDetailsBundle? {
        return PostDetailsBundle(
                intent.getStringExtra(PostDetailsBundle::postId.name) ?: "",
                intent.getIntExtra(PostDetailsBundle::tag.name, 0),
                intent.getStringExtra(PostDetailsBundle::newsFeed.name) ?: ""
        )
    }

    fun PostFeedBundle(intent: Intent): FeedBundle? {
        return FeedBundle(
                intent.getStringExtra(FeedBundle::userId.name) ?: "",
                intent.getStringExtra(FeedBundle::clubId.name) ?: ""
        )
    }

    private fun setBundlePostFeed(id: String, tag: Int = 0): Bundle {
        val bundle = Bundle()
        if (tag == 0) {
            bundle.putString(FeedBundle::userId.name, id)
        } else {
            bundle.putString(FeedBundle::clubId.name, id)
        }
        return bundle
    }

    fun addCourseDirection(activity: Activity, requestCode: Int, limit: Int, blackList: String, title: String) {
        startActivityForResult(activity, SelectCourseActivity::class.java, requestCode, setBundleCourse(blackList))
    }

    fun feedPostDirection(activity: Activity, id: String = "") {
        startActivity(activity, FeedsActivity::class.java, setBundlePostFeed(id))
    }

    fun feedClubDirection(fragment: Fragment, id: String = "") {
        startActivityForResult(fragment, FeedsActivity::class.java, 0, setBundlePostFeed(id, 1))
    }

    fun roundHistoryDirection(activity: Activity, id: String = "") {
        startActivity(activity, RoundHistoryActivity::class.java, setBundlePostFeed(id))
    }

    fun addCourseDirection(fragment: Fragment, requestCode: Int, limit: Int, blackList: String, title: String) {
        startActivityForResult(fragment, SelectCourseActivity::class.java, requestCode, setBundleCourse(blackList))
    }

    fun dispatchTakePictureCameraIntent(yourActivity: EasyGolfActivity<*>, photoFileHolder: File) {
        checkSelfPermission(Manifest.permission.CAMERA, Contains.REQUEST_CODE_CAMERA_PERMISSION, yourActivity) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                photoFileHolder.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            yourActivity,
                            "${yourActivity.packageName}.file_provider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    yourActivity.startActivityForResult(takePictureIntent, Contains.REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    fun dispatchTakePictureCameraIntent(fragment: EasyGolfFragment<*>, photoFileHolder: File) {
        checkSelfPermission(Manifest.permission.CAMERA, Contains.REQUEST_CODE_CAMERA_PERMISSION, fragment) {
            fragment.context?.let { context ->
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

                    photoFileHolder.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.file_provider",
                                it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        fragment.startActivityForResult(takePictureIntent, Contains.REQUEST_CODE_CAMERA)
                    }
                }
            }
        }
    }

    private fun setBundleGallery(maxPhotoSelect: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt(GalleryBundle::maxPhotoSelect.name, maxPhotoSelect)
        return bundle
    }

    fun pickPhotoGalleryDirection(fragment: EasyGolfFragment<*>, requestCode: Int, maxPhotoSelect: Int = 1) {
        startActivityForResult(fragment, EasyGolfGalleryActivity::class.java, requestCode, setBundleGallery(maxPhotoSelect))
    }

    fun pickPhotoGalleryDirection(activity: EasyGolfActivity<*>, requestCode: Int, maxPhotoSelect: Int = 1) {
        startActivityForResult(activity, EasyGolfGalleryActivity::class.java, requestCode, setBundleGallery(maxPhotoSelect))
    }

    fun getGalleryBundle(intent: Intent): GalleryBundle? {
        return GalleryBundle(
                intent.getStringExtra(GalleryBundle::result.name),
                intent.getIntExtra(GalleryBundle::maxPhotoSelect.name, 1)

        )
    }

    fun makeAPhoneCall(activity: EasyGolfActivity<*>, phone: String) {
        checkSelfPermission(Manifest.permission.CALL_PHONE, Contains.REQUEST_CODE_PHONE_CALL_PERMISSION, activity) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phone")
            activity.startActivity(callIntent)
        }
    }

    fun launchTurnByTurnNavigation(activity: EasyGolfActivity<*>, latitude: Double, longitude: Double, onError: (String?) -> Unit) {
        try {
            val gmmIntentUri =
                    Uri.parse("google.navigation:q=$latitude,$longitude")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            activity.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            onError(e.localizedMessage)
        }
    }

    fun scoreCardCourseDirection(context: Context, data: List<Scorecard>?) {
        val bundle = Bundle()
        bundle.putString(ScorecardScoreBundle::data.name, ScorecardScoreBundle.toData(data))
        startActivity(context, ScoreCardCourseActivity::class.java, bundle)
    }

    fun scoreCardCourseBundle(intent: Intent): ScorecardScoreBundle? {
        return intent.getStringExtra(ScorecardScoreBundle::data.name)?.let { data ->
            ScorecardScoreBundle(data)
        }
    }

    fun shareFile(context: Context, file: File, title: String? = "easyGolf share") {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
        )
        val fileUri = FileProvider.getUriForFile(context,
                "${context.packageName}.file_provider", file.absoluteFile)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        context.startActivity(Intent.createChooser(shareIntent, title))
    }

    fun feedbackGolfDirection(context: Context) {
        startActivity(context, FeedbackActivity::class.java)
    }

    fun endGameGolfDirection(context: Context, endGameBundle: EndGameBundle) {
        val bundle = Bundle()
        bundle.putString(EndGameBundle::roundId.name, endGameBundle.roundId)
        bundle.putString(EndGameBundle::teeType.name, endGameBundle.teeType)
        bundle.putString(EndGameBundle::scorecards.name, endGameBundle.scorecards)
        bundle.putInt(EndGameBundle::typeGame.name, endGameBundle.typeGame.value)
        startActivity(context, EndGameActivity::class.java, bundle)
    }

    fun buildEndGameBundle(endGameBundle: EndGameBundle): Bundle {
        val bundle = Bundle()
        bundle.putString(EndGameBundle::roundId.name, endGameBundle.roundId)
        bundle.putString(EndGameBundle::teeType.name, endGameBundle.teeType)
        bundle.putString(EndGameBundle::scorecards.name, endGameBundle.scorecards)
        bundle.putInt(EndGameBundle::typeGame.name, endGameBundle.typeGame.value)
        return bundle
    }

    fun endGameBundle(intent: Intent): EndGameBundle? {
        return intent.getStringExtra(EndGameBundle::roundId.name)?.let { roundId ->
            val typeGame = when (intent.getIntExtra(PlayGolfBundle::typeGame.name, PlayGolfBundle.TypeGame.EXPLORE.value)) {
                PlayGolfBundle.TypeGame.EXPLORE.value -> PlayGolfBundle.TypeGame.EXPLORE
                PlayGolfBundle.TypeGame.NEW_GAME.value -> PlayGolfBundle.TypeGame.NEW_GAME
                PlayGolfBundle.TypeGame.BATTLE_GAME.value -> PlayGolfBundle.TypeGame.BATTLE_GAME
                PlayGolfBundle.TypeGame.UNKNOWN.value -> PlayGolfBundle.TypeGame.UNKNOWN
                else -> PlayGolfBundle.TypeGame.EXPLORE
            }
            EndGameBundle(roundId,
                    intent.getStringExtra(EndGameBundle::teeType.name),
                    intent.getStringExtra(PlayGolfBundle::scorecards.name),
                    typeGame
            )
        }
    }

    fun endGameBundle(bundle: Bundle?): EndGameBundle? {
        return bundle?.getString(EndGameBundle::roundId.name)?.let { roundId ->
            val typeGame = when (bundle.getInt(PlayGolfBundle::typeGame.name, PlayGolfBundle.TypeGame.EXPLORE.value)) {
                PlayGolfBundle.TypeGame.EXPLORE.value -> PlayGolfBundle.TypeGame.EXPLORE
                PlayGolfBundle.TypeGame.NEW_GAME.value -> PlayGolfBundle.TypeGame.NEW_GAME
                PlayGolfBundle.TypeGame.BATTLE_GAME.value -> PlayGolfBundle.TypeGame.BATTLE_GAME
                PlayGolfBundle.TypeGame.UNKNOWN.value -> PlayGolfBundle.TypeGame.UNKNOWN
                else -> PlayGolfBundle.TypeGame.EXPLORE
            }
            EndGameBundle(roundId,
                    bundle.getString(EndGameBundle::teeType.name),
                    bundle.getString(PlayGolfBundle::scorecards.name),
                    typeGame
            )
        }
    }

    fun enterPassScoreDirection(context: Context, courseId: String, teeType: String) {
        val bundle = Bundle()
        bundle.putString(PassScoreBundle::courseId.name, courseId)
        bundle.putString(PassScoreBundle::teeType.name, teeType)
        startActivity(context, EnterPassScoreActivity::class.java, bundle)
    }

    fun passScoreBundle(intent: Intent): PassScoreBundle? {
        return intent.getStringExtra(PassScoreBundle::courseId.name)?.let { courseId ->
            intent.getStringExtra(PassScoreBundle::teeType.name)?.let { teeType ->
                PassScoreBundle(courseId, teeType)
            }
        }
    }

    fun forgotPasswordDirection(context: Context, unregisterBundle: UnregisterBundle? = null) {
        val bundle = unregisterBundle?.let { unregisterBundleSocial ->
            val accessBundle = Bundle()
            accessBundle.putString(UnregisterBundle::type.name, unregisterBundleSocial.type)
            accessBundle.putString(UnregisterBundle::accessToken.name, unregisterBundleSocial.accessToken)
            accessBundle.putString(UnregisterBundle::email.name, unregisterBundleSocial.email)
            accessBundle
        }
        startActivity(context, ForgotPasswordActivity::class.java, bundle)
    }

    fun forgotPasswordBundle(intent: Intent): UnregisterBundle? {
        return intent.getStringExtra(UnregisterBundle::type.name)?.let { type ->
            intent.getStringExtra(UnregisterBundle::accessToken.name)?.let { accessToken ->
                UnregisterBundle(
                        type,
                        accessToken,
                        intent.getStringExtra(UnregisterBundle::email.name)
                )
            }
        }
    }

    fun verifyCodeDirection(context: Context, verifyCodeBundle: VerifyCodeBundle) {
        val bundle = Bundle()
        bundle.putString(VerifyCodeBundle::phone.name, verifyCodeBundle.phone)
        bundle.putString(VerifyCodeBundle::countryCode.name, verifyCodeBundle.countryCode)
        startActivity(context, VerifyCodeActivity::class.java, bundle)
    }

    fun verifyCodeBundle(intent: Intent): VerifyCodeBundle? {
        return intent.getStringExtra(VerifyCodeBundle::phone.name)?.let { phone ->
            intent.getStringExtra(VerifyCodeBundle::countryCode.name)?.let { countryCode ->
                VerifyCodeBundle(phone, countryCode)
            }
        }
    }

    fun accountUpdateDirection(context: Activity, accountUpdateBundle: AccountUpdateBundle) {
        val bundle = Bundle()
        bundle.putString(accountUpdateBundle::user.name, accountUpdateBundle.user)
        bundle.putBoolean(accountUpdateBundle::tag.name, accountUpdateBundle.tag)
        startActivityForResult(context, AccountUpdateActivity::class.java, UPDATE_REQEUST_CODE, bundle)
    }

    fun countryDirection(activity: EasyGolfActivity<*>) {
        startActivityForResult(activity, CountryActivity::class.java, Contains.REQUEST_CODE_COUNTRY)
    }

    fun countryResultDirection(country: Country): Intent {
        val intent = Intent()
        intent.putExtra(CountryBundle::country.name, CountryBundle.toData(country))
        return intent
    }

    fun countryBundle(intent: Intent?): CountryBundle? {
        return intent?.getStringExtra(CountryBundle::country.name)?.let { countryJson ->
            CountryBundle(countryJson)
        }
    }

    fun scorecardDirection(context: Context, endGameBundle: EndGameBundle) {
        val bundle = Bundle()
        bundle.putString(EndGameBundle::roundId.name, endGameBundle.roundId)
        bundle.putString(EndGameBundle::teeType.name, endGameBundle.teeType)
        bundle.putString(EndGameBundle::scorecards.name, endGameBundle.scorecards)
        bundle.putInt(EndGameBundle::typeGame.name, endGameBundle.typeGame.value)
        startActivity(context, ScorecardActivity::class.java, bundle)
    }

    fun notificationModelBundle(intent: Intent): NotificationModel? {
        return intent.extras?.let { bundle ->
            val id = bundle.getString(NotificationModel::id.name)
            val title = bundle.getString(NotificationModel::title.name)
            val body = bundle.getString(NotificationModel::body.name)
            bundle.getString(NotificationModel::type.name)?.let { typeNotification ->
                NotificationModel(
                        id, title, body, typeNotification
                )
            }
        }
    }
}