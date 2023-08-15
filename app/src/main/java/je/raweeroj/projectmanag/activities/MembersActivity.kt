package je.raweeroj.projectmanag.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import je.raweeroj.projectmanag.R
import je.raweeroj.projectmanag.adapters.MemberListItemsAdapter
import je.raweeroj.projectmanag.databinding.ActivityMembersBinding
import je.raweeroj.projectmanag.databinding.DialogSearchMemberBinding
import je.raweeroj.projectmanag.firebase.FirestoreClass
import je.raweeroj.projectmanag.models.Board
import je.raweeroj.projectmanag.models.User
import je.raweeroj.projectmanag.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private lateinit var mAssignedMembersList : ArrayList<User>
    private var anyChangeMade : Boolean = false

    private var binding:ActivityMembersBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this,mBoardDetails.assignedTo
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member ->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(anyChangeMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }



        private inner class SendNotificationToUser(val boardName:String, val token:String){

            fun startApiCall() {
                showProgressDialog("Notification Logic")
                lifecycleScope.launch(Dispatchers.IO) {
                    val stringResult=makeApiCall()
                    afterCallFinish(stringResult)
                }
            }


            private fun makeApiCall(): String {
                Log.i("****makeApiCall","from MemberActivity - Receivers FCM key = $token")
                var result: String

                /**
                 * https://developer.android.com/reference/java/net/HttpURLConnection
                 *
                 * You can use the above url for Detail understanding of HttpURLConnection class
                 */
                var connection: HttpURLConnection? = null
                try {
                    val url = URL(Constants.FCM_BASE_URL) // Base Url
                    connection = url.openConnection() as HttpURLConnection

                    /**
                     * A URL connection can be used for input and/or output.  Set the DoOutput
                     * flag to true if you intend to use the URL connection for output,
                     * false if not.  The default is false.
                     */
                    connection.doOutput = true
                    connection.doInput = true

                    /**
                     * Sets whether HTTP redirects should be automatically followed by this instance.
                     * The default value comes from followRedirects, which defaults to true.
                     */
                    connection.instanceFollowRedirects = false

                    /**
                     * Set the method for the URL request, one of:
                     *  POST
                     */
                    connection.requestMethod = "POST"

                    /**
                     * Sets the general request property. If a property with the key already
                     * exists, overwrite its value with the new value.
                     */
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("charset", "utf-8")
                    connection.setRequestProperty("Accept", "application/json")


                    // TODO (Step 3: Add the firebase Server Key.)
                    // START
                    // In order to find your Server Key or authorization key, follow the below steps:
                    // 1. Goto Firebase Console.
                    // 2. Select your project.
                    // 3. Firebase Project Setting
                    // 4. Cloud Messaging
                    // 5. Finally, the SerkeyKey.
                    // For Detail understanding visit the link: https://android.jlelse.eu/android-push-notification-using-firebase-and-advanced-rest-client-3858daff2f50
                    connection.setRequestProperty(
                        Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                    )
                    // END

                    /**
                     * Some protocols do caching of documents.  Occasionally, it is important
                     * to be able to "tunnel through" and ignore the caches (e.g., the
                     * "reload" button in a browser).  If the UseCaches flag on a connection
                     * is true, the connection is allowed to use whatever caches it can.
                     *  If false, caches are to be ignored.
                     *  The default value comes from DefaultUseCaches, which defaults to
                     * true.
                     */
                    connection.useCaches = false

                    /**
                     * Creates a new data output stream to write data to the specified
                     * underlying output stream. The counter written is set to zero.
                     */
                    val wr = DataOutputStream(connection.outputStream)

                    // TODO (Step 4: Create a notification data payload.)
                    // START
                    // Create JSONObject Request
                    val jsonRequest = JSONObject()

                    // Create a data object
                    val dataObject = JSONObject()
                    // Here you can pass the title as per requirement as here we have added some text and board name.
                    dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the Board $boardName")
                    // Here you can pass the message as per requirement as here we have added some text and appended the name of the Board Admin.
                    dataObject.put(
                        Constants.FCM_KEY_MESSAGE,
                        "You have been assigned to the new board by ${mAssignedMembersList[0].name}"
                    )

                    // Here add the data object and the user's token in the jsonRequest object.
                    jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                    jsonRequest.put(Constants.FCM_KEY_TO, token)
                    // END

                    /**
                     * Writes out the string to the underlying output stream as a
                     * sequence of bytes. Each character in the string is written out, in
                     * sequence, by discarding its high eight bits. If no exception is
                     * thrown, the counter written is incremented by the
                     * length of s.
                     */
                    wr.writeBytes(jsonRequest.toString())
                    wr.flush() // Flushes this data output stream.
                    wr.close() // Closes this output stream and releases any system resources associated with the stream

                    val httpResult: Int =
                        connection.responseCode // Gets the status code from an HTTP response message.
                    println(" ****** SendNotificationToUser httpResult: $httpResult")
                    if (httpResult == HttpURLConnection.HTTP_OK) {

                        /**
                         * Returns an input stream that reads from this open connection.
                         */
                        val inputStream = connection.inputStream

                        /**
                         * Creates a buffering character-input stream that uses a default-sized input buffer.
                         */
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val sb = StringBuilder()
                        var line: String?
                        try {
                            /**
                             * Reads a line of text.  A line is considered to be terminated by any one
                             * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                             * followed immediately by a linefeed.
                             */
                            while (reader.readLine().also { line = it } != null) {
                                sb.append(line + "\n")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            try {
                                /**
                                 * Closes this input stream and releases any system resources associated
                                 * with the stream.
                                 */
                                inputStream.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        result = sb.toString()
                        println("****** SendNotificationToUser result $result")
                    } else {
                        /**
                         * Gets the HTTP response message, if any, returned along with the
                         * response code from a server.
                         */
                        result = connection.responseMessage
                    }

                } catch (e: SocketTimeoutException) {
                    result = "Connection Timeout"
                } catch (e: Exception) {
                    result = "Error : " + e.message
                } finally {
                    connection?.disconnect()
                }

                // You can notify with your result to onPostExecute.


                return result
            }

            private fun afterCallFinish(stringResult: String?) {
                hideProgressDialog()
                Log.i("NotifToUser","${stringResult}")
            }

        }


    private fun dialogSearchMember(){
        //var dialogBinding : DialogSearchMemberBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_search_member)

        var tvAdd = dialog.findViewById<TextView>(R.id.tv_add)
        var tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
        var etEmailSearchMember = dialog.findViewById<EditText>(R.id.et_email_search_member)

                tvAdd.setOnClickListener {
            val email = etEmailSearchMember.text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this,email)
            }else{
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter members email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun setUpMembersList(list:ArrayList<User>){

        mAssignedMembersList = list
        hideProgressDialog()

        binding?.rvMembersList?.layoutManager = LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this,list)
        binding?.rvMembersList?.adapter = adapter
    }

    fun memberDetails(user:User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this ,mBoardDetails,user)
    }

    private fun  setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_color_white_24dp)
            actionBar.title = resources.getString(R.string.members)
        }

        binding?.toolbarMembersActivity?.setNavigationOnClickListener { onBackPressed() }


    }

    fun memberAssignSuccess(user: User){

        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangeMade = true

        setUpMembersList(mAssignedMembersList)
        //showProgressDialog(resources.getString(R.string.please_wait))
        SendNotificationToUser(mBoardDetails.name,user.fcmToken).startApiCall()

    }
}