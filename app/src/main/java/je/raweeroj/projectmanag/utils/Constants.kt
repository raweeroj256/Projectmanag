package je.raweeroj.projectmanag.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import je.raweeroj.projectmanag.R

object Constants {

    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_TOKEN : String ="fcmToken"
    const val DOCUMENT_ID: String = "documentId"
    const val USERS: String = "Users"
    const val TASK_LIST:String = "taskList"
    const val BOARD_DETAIL : String = "board_detail"
    const val BOARDS : String = "boards"

    const val IMAGE:String = "image"
    const val NAME: String = "name"
    const val MOBILE : String = "mobile"
    const val ASSIGNED_TO : String="assignedTo"
    const val ID:String="id"
    const val EMAIL:String = "email"
    const val TASK_LIST_ITEM_POSITION : String = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION : String = "card_list_item_position"
    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val SELECT:String = "Select"
    const val UN_SELECT:String = "UnSelect"
    const val PROJECTMANAJ_PREFERENCES = "ProjectmanagePrefs"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION : String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String= "AAAABgPTwFA:APA91bHuKBt_vrV-q5hQozg8W0TJbjjFoXRkNUBarHxUI-UcMd2ASS1sR-y1X4WfJZADj55FZUhNAdQ1pec3BDnm6R6MSXwa23vgq10unKEr55JpMEDXorKylE9mEqPhEGrK3I5NMug-"
    const val FCM_KEY_TITLE:String="title"
    const val FCM_KEY_MESSAGE:String="message"
    const val FCM_KEY_DATA:String="data"
    const val FCM_KEY_TO:String = "to"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2



}