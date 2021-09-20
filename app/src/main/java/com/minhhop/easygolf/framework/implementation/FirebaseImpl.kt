package com.minhhop.easygolf.framework.implementation

import com.google.firebase.database.*
import com.minhhop.core.data.datasource.FirebaseDataSource
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.MatchGolf
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.golf.ScorecardModel
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.extension.await
import com.minhhop.easygolf.framework.extension.getSingleValue
import com.minhhop.easygolf.framework.models.firebase.DataScoreGolfFirebase
import com.minhhop.easygolf.framework.models.firebase.MemberFirebase
import com.minhhop.easygolf.framework.models.firebase.UserFirebase

class FirebaseImpl(private val realmManager: RealmManager) : FirebaseDataSource {

    override suspend fun getBattleRoundFindFirst(onComplete: (BattleRound?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.getProfile()?.let { user ->
            try {
                FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                        .child("battles").orderByKey().getSingleValue().let { dataSnapshot ->
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                val element = dataSnapshot.children.elementAt(0)
                                val result = element.getValue(BattleRound::class.java)
                                result?.id = element.key
                                onComplete(result)
                            } else {
                                onComplete(null)
                            }
                        }
            } catch (e: Exception) {
                errorResponse(ErrorResponse.commonError(e.localizedMessage))
            }
        } ?: onComplete(null)
    }

    override fun getBattleRound(roundId: String, userId: String, onComplete: (BattleRound?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {

        FirebaseDatabase.getInstance().reference.child("users").child(userId)
                .child("battles").orderByChild("round_id").equalTo(roundId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                val element = dataSnapshot.children.elementAt(0)
                                val result = element.getValue(BattleRound::class.java)
                                result?.id = element.key
                                onComplete(result)
                            } else {
                                onComplete(null)
                            }

                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override suspend fun removeBattleRound(roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        try {
            FirebaseDatabase.getInstance().reference.child("rounds").child(roundId)
                    .child("members").getSingleValue().let { dataSnapshot ->
                        if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                            for (element in dataSnapshot.children) {
                                element.getValue(MemberFirebase::class.java)?.let { memberFirebase ->
                                    removeBattleUser(roundId, memberFirebase.id, {}, {})
                                }
                            }
                            FirebaseDatabase.getInstance().reference.child("rounds").child(roundId).removeValue().await()
                            onComplete()
                        } else {
                            onComplete()
                        }
                    }
        } catch (e: Exception) {
            errorResponse(ErrorResponse.commonError(e.localizedMessage))
        }


        FirebaseDatabase.getInstance().reference.child("rounds").child(roundId)
                .child("members")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                for (element in dataSnapshot.children) {
                                    element.getValue(MemberFirebase::class.java)?.let { memberFirebase ->
                                        removeBattleUser(roundId, memberFirebase.id, {}, {})
                                    }
                                }
                                FirebaseDatabase.getInstance().reference.child("rounds").child(roundId).removeValue()
                                        .addOnCompleteListener {
                                            onComplete()
                                        }
                            } else {
                                onComplete()
                            }
                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun removeMemberFromBattle(roundId: String, user: User, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        removeBattleUser(roundId, user.id, {}, {})
        removeDataScoreMember(roundId, user)
        removeMember(roundId, user, onComplete, errorResponse)
    }

    /**
     * remove battle in users/userId/battles
     * */
    private fun removeBattleUser(roundId: String, userId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        removeValueWithQuery(FirebaseDatabase.getInstance().reference.child("users")
                .child(userId).child("battles")
                .orderByChild(BattleRound::round_id.name).equalTo(roundId), {
            onComplete()
        }, errorResponse)
    }

    /**
     * remove member in battle round
     * */
    private fun removeMember(roundId: String, user: User, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        removeValueWithQuery(FirebaseDatabase.getInstance().reference.child("rounds")
                .child(roundId).child("members")
                .orderByChild(MemberFirebase::id.name).equalTo(user.id), {
            onComplete()
        }, errorResponse)
    }

    /**
     * remove data score of member in battle round
     * */
    private fun removeDataScoreMember(roundId: String, user: User) {
        FirebaseDatabase.getInstance().reference.child("rounds")
                .child(roundId)
                .child("holes").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            for (element in snapshot.children) {
                                for (item in element.children) {
                                    if (item.key == user.id) {
                                        item.ref.removeValue()
                                    }
                                }
                            }
                        }

                    }

                })
    }

    /**
     * remove battle round, keep battle with id,
     * */
    private fun removeAnotherBattle(roundIdKeeper: String, user: User, withPending: Boolean? = null) {
        FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                .child("battles")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {}
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                for (element in dataSnapshot.children) {
                                    element.getValue(BattleRound::class.java)?.let { battleRound ->
                                        val checkPending = if (withPending == null) {
                                            true
                                        } else {
                                            battleRound.is_pending == withPending
                                        }
                                        if (battleRound.round_id != roundIdKeeper && checkPending) {
                                            battleRound.round_id?.let { roundGolfId ->
                                                removeMemberFromBattle(roundGolfId, user, {}, {})
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }
                })
    }

    private fun removeValueWithQuery(query: Query, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                errorResponse(ErrorResponse.commonError(databaseError.message))
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (snap in dataSnapshot.children) {
                        snap.ref.removeValue()
                    }
                    onComplete()
                }
            }
        })
    }

    override fun getBattleRoundWithCourse(courseId: String, userId: String?, onComplete: (BattleRound?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        if (userId.isNullOrEmpty()) {
            realmManager.getProfile()?.id
        } else {
            userId
        }?.let { currentUserId ->
            FirebaseDatabase.getInstance().reference.child("users").child(currentUserId)
                    .child("battles").orderByChild("course_id").equalTo(courseId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(dataBaseError: DatabaseError) {
                            errorResponse(ErrorResponse.commonError(dataBaseError.message))
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            try {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    val element = dataSnapshot.children.elementAt(0)
                                    val result = element.getValue(BattleRound::class.java)
                                    result?.id = element.key
                                    onComplete(result)
                                } else {
                                    onComplete(null)
                                }

                            } catch (e: Exception) {
                                errorResponse(ErrorResponse.commonError(e.localizedMessage))
                            }
                        }
                    })
        } ?: onComplete(null)

    }

    override fun getProfileUser(id: String, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users").child(id).child("profile")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                val result = dataSnapshot.getValue(UserFirebase::class.java)
                                result?.id = id
                                onComplete(result?.toUser())
                            } else {
                                onComplete(null)
                            }

                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun fetchMembersInBattle(roundId: String, onResult: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("rounds").child(roundId)
                .child("members")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                val currentUser = realmManager.getProfile()
                                for (element in dataSnapshot.children) {
                                    element.getValue(MemberFirebase::class.java)?.let { memberFirebase ->
                                        if (currentUser?.id != memberFirebase.id) {
                                            getProfileUser(memberFirebase.id, onResult, errorResponse)
                                        }
                                    }

                                }
                            } else {
                                onResult(null)
                            }

                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun fetchMembersWithIdInBattle(roundId: String, onResult: (List<String>?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("rounds").child(roundId)
                .child("members")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                val listMemberId = ArrayList<String>()
                                for (element in dataSnapshot.children) {
                                    element.getValue(MemberFirebase::class.java)?.let { memberFirebase ->
                                        listMemberId.add(memberFirebase.id)
                                    }
                                }
                                onResult(listMemberId)
                            } else {
                                onResult(null)
                            }

                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun fetchDataScoreGolfForMembersInBattle(roundGolf: RoundGolf, onResult: (HashMap<String, ArrayList<MatchGolf?>?>?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        roundGolf.id?.let { roundGolfId ->
            roundGolf.holes?.let { holes ->
                FirebaseDatabase.getInstance().reference.child("rounds")
                        .child(roundGolfId)
                        .child("holes").orderByValue()
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(dataBaseError: DatabaseError) {
                                errorResponse(ErrorResponse.commonError(dataBaseError.message))
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        val result = HashMap<String, ArrayList<MatchGolf?>?>()
                                        realmManager.getProfile()?.let { user ->
                                            for (snap in dataSnapshot.children) {
                                                snap.key?.toInt()?.let { holeNumber ->
                                                    for (snapMember in snap.children) {
                                                        snapMember.key?.let { memberId ->
                                                            if (memberId != user.id) {
                                                                if (!result.containsKey(memberId) || result[memberId] == null) {
                                                                    result[memberId] = ArrayList()
                                                                }
                                                                snapMember.getValue(DataScoreGolfFirebase::class.java)?.toDataScoreGolf()?.let { data ->
                                                                    data.number = holeNumber
                                                                    result[memberId]?.add(
                                                                            MatchGolf.fromDataScoreGolf(data,
                                                                                    /**
                                                                                     * find holeId in list hole of round
                                                                                     * */
                                                                                    holes[holeNumber - 1].hole_id
                                                                            )
                                                                    )
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            onResult(result)
                                        }

                                    } else {
                                        onResult(null)
                                    }
                                } catch (e: Exception) {
                                    errorResponse(ErrorResponse.commonError(e.localizedMessage))
                                }
                            }
                        })
            } ?: onResult(null)

        } ?: onResult(null)

    }

    override fun addMembersToBattle(roundId: String, courseId: String, users: List<User>, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val dbMembers = FirebaseDatabase.getInstance().reference.child("rounds")
                .child(roundId)
                .child("members")
        realmManager.getProfile()?.let { currentUser ->
            users.forEach { user ->
                FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                        .child("battles").push().setValue(BattleRound(
                                course_id = courseId, is_host = currentUser.id == user.id, round_id = roundId,
                                is_pending = currentUser.id != user.id
                        )).addOnCompleteListener {
                            /**
                             * remove battle with pending true for other member not current user
                             * */
                            if (user.id != currentUser.id) {
                                removeAnotherBattle(roundId, user, true)
                            }
                        }

                val key = dbMembers.push()
                key.setValue(MemberFirebase(user.id))

                updateHandicapUser(user, {}, {})
            }
        }

        onComplete()
    }

    override suspend fun postScore(roundId: String, numberHole: String, user: User, data: DataScoreGolf, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {

        try {
            FirebaseDatabase.getInstance().reference.child("rounds")
                    .child(roundId)
                    .child("holes").child(numberHole).child(user.id).setValue(
                            DataScoreGolfFirebase.fromDataScoreGolf(data)
                    ).await()
            onComplete()
        } catch (e: Exception) {
            errorResponse(ErrorResponse.commonError(e.localizedMessage))
        }
    }

    override fun getScoreUser(roundId: String, numberHole: String, user: User, onComplete: (DataScoreGolf?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("rounds")
                .child(roundId)
                .child("holes").child(numberHole).child(user.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists()) {
                                val result = dataSnapshot.getValue(DataScoreGolfFirebase::class.java)
                                onComplete(result?.toDataScoreGolf())
                            } else {
                                onComplete(null)
                            }
                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun getDataScoreAtRound(roundId: String, onComplete: (List<ScorecardModel>?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.getProfile()?.let { targetUser ->
            fetchMembersWithIdInBattle(roundId, { members ->
                val result = HashMap<String, ArrayList<DataScoreGolf>>()
                members?.forEach { member ->
                    if (!result.containsKey(member)) {
                        result[member] = ArrayList()
                    }
                }

                FirebaseDatabase.getInstance().reference.child("rounds")
                        .child(roundId)
                        .child("holes").orderByValue()
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(dataBaseError: DatabaseError) {
                                errorResponse(ErrorResponse.commonError(dataBaseError.message))
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                try {
                                    val listScorecard = ArrayList<ScorecardModel>()
                                    if (dataSnapshot.exists()) {
                                        for (snap in dataSnapshot.children) {
                                            snap.key?.let { holeNumber ->
                                                for (snapScoreByUser in snap.children) {
                                                    snapScoreByUser.key?.let { userId ->
                                                        snapScoreByUser.getValue(DataScoreGolfFirebase::class.java)?.toDataScoreGolf()?.let { data ->
                                                            data.number = holeNumber.toInt()
                                                            if (!result.containsKey(userId)) {
                                                                result[userId] = ArrayList()
                                                            }
                                                            result[userId]?.add(data)
                                                        }
                                                    }

                                                }

                                            }
                                        }

                                        for ((key, data) in result) {
                                            if (key == targetUser.id && listScorecard.isNotEmpty()) {
                                                listScorecard.add(0,
                                                        ScorecardModel(key, data)
                                                )
                                            } else {
                                                listScorecard.add(ScorecardModel(key, data))
                                            }
                                        }
                                        onComplete(listScorecard)
                                    } else {
                                        for ((key, data) in result) {
                                            if (key == targetUser.id && listScorecard.isNotEmpty()) {
                                                listScorecard.add(0,
                                                        ScorecardModel(key, data)
                                                )
                                            } else {
                                                listScorecard.add(ScorecardModel(key, data))
                                            }
                                        }
                                        onComplete(listScorecard)
                                    }
                                } catch (e: Exception) {
                                    errorResponse(ErrorResponse.commonError(e.localizedMessage))
                                }
                            }
                        })
            }, errorResponse)

        } ?: onComplete(null)

    }

    override suspend fun createBattleGame(roundId: String, courseId: String, friends: List<User>?, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.deleteDataScoreGolfRound(onComplete) {
            errorResponse(ErrorResponse.commonError(it))
        }
        /**
         * create battle for current user, with host
         * */
        realmManager.getProfile()?.let { user ->
            try {
                FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                        .child("battles").getSingleValue().let { snapshot ->
                            for (element in snapshot.children) {
                                try {
                                    element.getValue(BattleRound::class.java)?.let { battleRound ->
                                        battleRound.round_id?.let { roundId ->
                                            if (battleRound.is_host == true) {
                                                removeBattleRound(roundId, { }, {})
                                            } else {
                                                removeMemberFromBattle(roundId, user, {}, {})
                                            }
                                        }

                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            val members = ArrayList<User>()
                            members.add(user)
                            friends?.let { clearFriends ->
                                members.addAll(clearFriends.toList())
                            }
                            addMembersToBattle(roundId, courseId, members, onComplete, errorResponse)
                        }
            } catch (e: Exception) {
                errorResponse(ErrorResponse.commonError(e.localizedMessage))
            }
        }
    }

    override fun updateHandicapUser(user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                .child("profile").child("handicap").setValue(user.handicap)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onComplete(user)
                    } else {
                        onComplete(null)
                    }
                }
                .addOnFailureListener {
                    errorResponse(ErrorResponse.commonError(it.localizedMessage))
                }
    }

    override fun updateStatusPendingInBattle(isPending: Boolean, roundId: String, user: User, onComplete: (User?) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                .child("battles").orderByChild(BattleRound::round_id.name).equalTo(roundId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        try {
                            if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                val element = dataSnapshot.children.elementAt(0)
                                element.ref.child(BattleRound::is_pending.name).setValue(isPending)
                                removeAnotherBattle(roundId, user)
                            } else {
                                onComplete(null)
                            }

                        } catch (e: Exception) {
                            errorResponse(ErrorResponse.commonError(e.localizedMessage))
                        }
                    }
                })
    }

    override fun changeTeeTypeInBattle(teeType: String, roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.getProfile()?.let { user ->
            FirebaseDatabase.getInstance().reference.child("users").child(user.id)
                    .child("battles").orderByChild(BattleRound::round_id.name).equalTo(roundId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(dataBaseError: DatabaseError) {
                            errorResponse(ErrorResponse.commonError(dataBaseError.message))
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            try {
                                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                                    val element = dataSnapshot.children.elementAt(0)
                                    element.ref.child(BattleRound::tee_type.name).setValue(teeType)
                                } else {
                                    onComplete()
                                }

                            } catch (e: Exception) {
                                errorResponse(ErrorResponse.commonError(e.localizedMessage))
                            }
                        }
                    })
        } ?: onComplete()

    }

    override fun isRoundExit(roundId: String, onResult: (Boolean) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("rounds")
                .child(roundId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dataBaseError: DatabaseError) {
                        errorResponse(ErrorResponse.commonError(dataBaseError.message))
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        onResult(dataSnapshot.exists() && dataSnapshot.hasChildren())
                    }
                })
    }
}