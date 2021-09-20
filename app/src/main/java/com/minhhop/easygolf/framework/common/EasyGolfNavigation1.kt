package com.minhhop.easygolf.framework.common

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.minhhop.easygolf.framework.bundle.*
import com.minhhop.easygolf.framework.bundle.HomeBundle.TYPE.*
import com.minhhop.easygolf.listeners.OnComplete
import com.minhhop.easygolf.presentation.golf.play.PlayGolfActivity
import com.minhhop.easygolf.presentation.home.EasyGolfHomeActivity
import com.minhhop.easygolf.presentation.verify_code.VerifyCodeActivity
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.views.activities.MessengerActivity

@Deprecated("EasyGolfNavigation1 will remove in v2", ReplaceWith("EasyGolfNavigation"))
class EasyGolfNavigation1 {
    companion object{

        fun verifyCodeDirection(context: Context, verifyCodeBundle: VerifyCodeBundle) {
            val bundle = Bundle()
            bundle.putString(VerifyCodeBundle::phone.name,verifyCodeBundle.phone)
            bundle.putString(VerifyCodeBundle::countryCode.name,verifyCodeBundle.countryCode)
            startActivity(context, VerifyCodeActivity::class.java,bundle)
        }

        fun verifyCodeBundle(intent: Intent): VerifyCodeBundle {
            val phone = intent.getStringExtra(VerifyCodeBundle::phone.name)
            val countryCode = intent.getStringExtra(VerifyCodeBundle::phone.name)
            return VerifyCodeBundle(phone, countryCode)
        }

        fun homeDirection(context: Context) {
            startActivity(context, EasyGolfHomeActivity::class.java)
        }
        fun homeDirection(context: Context, homeBundle: HomeBundle?){
            val bundle = Bundle()
            homeBundle?.let {
                val type = when(it.type){
                    NIL -> {
                        null
                    }
                    CHAT -> {
                        bundle.putString(HomeBundle::channelId.name,it.channelId)
                        bundle.putBoolean(HomeBundle::group.name,it.group)
                        "CHAT"
                    }
                    ROUND_INVITE ->{
                        bundle.putString(HomeBundle::round.name,it.round)
                        bundle.putString(HomeBundle::clubId.name,it.clubId)
                        bundle.putString(HomeBundle::courseId.name,it.courseId)
                        bundle.putBoolean(HomeBundle::playWithBattle.name,it.playWithBattle)
                        "ROUND_INVITE"
                    }
                }
                bundle.putString(Contains.EXTRA_TYPE_NOTIFICATION, type)
            }

            startActivity(context,EasyGolfHomeActivity::class.java,bundle)
        }

        fun homeDirection(intent: Intent,context: Context){
            if (intent.extras != null) {
                val typeNotification = intent.getStringExtra(Contains.EXTRA_TYPE_NOTIFICATION)
                when(HomeBundle.whatType(typeNotification)){
                    NIL -> {}
                    CHAT -> {
                        val channelID = intent.getStringExtra(HomeBundle::channelId.name)
                        val isGroupChat = intent.getBooleanExtra(HomeBundle::group.name, false)
                        messageDirection(context, MessageBundle(channelID,isGroupChat))
                    }
                    ROUND_INVITE -> {
                        val isPlayWithBattle = intent.getBooleanExtra(HomeBundle::playWithBattle.name,false)
                        val roundIdBattle = intent.getStringExtra(HomeBundle::round.name)
                        val clubId = intent.getStringExtra(HomeBundle::clubId.name)
                        val courseId = intent.getStringExtra(HomeBundle::courseId.name)
                        /**
                         * TODO remove data round golf
                         *  DatabaseService.getInstance().removeDataRoundGolf(mIdClub, OnComplete {
                            val bundle = Bundle()
                            bundle.putBoolean(Contains.EXTRA_PLAY_WITH_BATTLE, true)
                            bundle.putString(Contains.EXTRA_ID_ROUND_BATTLE, mIdRoundBattle)
                            bundle.putString(Contains.EXTRA_ID_CLUB, mIdClub)
                            bundle.putString(Contains.EXTRA_ID_COURSE, mIdCourse)
                            startActivity(PlayGolfActivityOld::class.java, bundle)
                            }, false)
                         * */

                        DatabaseService.getInstance().removeDataRoundGolf(clubId, OnComplete {
                            playGolfDirection(context, PlayGolfBundle1(isPlayWithBattle,roundIdBattle,clubId,courseId))
                        })

                    }
                }

            }
        }

        fun playGolfBundle(intent: Intent):PlayGolfBundle1?{
            val isPlayWithBattle = intent.getBooleanExtra(PlayGolfBundle1::playWithBattle.name,false)
            val roundIdBattle = intent.getStringExtra(PlayGolfBundle1::round.name)
            val clubId = intent.getStringExtra(PlayGolfBundle1::clubId.name)
            val courseId = intent.getStringExtra(PlayGolfBundle1::courseId.name)
            val exitGameBattle = intent.getBooleanExtra(PlayGolfBundle1::exitGameBattle.name,false)
            return PlayGolfBundle1(isPlayWithBattle,roundIdBattle,clubId,courseId,exitGameBattle)
        }

        fun playGolfDirection(context: Context,playGolfBundle: PlayGolfBundle1?){
            val bundle = Bundle()
            playGolfBundle?.apply {
                bundle.putString(PlayGolfBundle1::round.name,this.round)
                bundle.putString(PlayGolfBundle1::clubId.name,this.clubId)
                bundle.putString(PlayGolfBundle1::courseId.name,this.courseId)
                bundle.putBoolean(PlayGolfBundle1::playWithBattle.name,this.playWithBattle)
                bundle.putBoolean(PlayGolfBundle1::exitGameBattle.name,this.exitGameBattle)
            }
            startActivity(context, PlayGolfActivity::class.java,bundle)
        }

        fun messageDirection(context: Context, messageBundle: MessageBundle?){
            val bundle = Bundle()
            messageBundle?.apply {
                bundle.putString(MessageBundle::channelId.name,this.channelId)
                bundle.putBoolean(MessageBundle::group.name,this.group)
            }
            startActivity(context,MessengerActivity::class.java,bundle)
        }

        fun messageBundle(intent: Intent): MessageBundle {
            val channelId = intent.getStringExtra(MessageBundle::channelId.name)
            val isGroup = intent.getBooleanExtra(MessageBundle::group.name,false)
            return MessageBundle(channelId,isGroup)
        }

        private fun startActivity(context: Context,cl:Class<*>,bundle: Bundle? = null){
            val intent = Intent(context,cl)
            bundle?.let {
                intent.putExtras(it)
            }
            context.startActivity(intent)
        }
    }
}
