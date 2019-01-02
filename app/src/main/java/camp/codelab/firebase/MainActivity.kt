package camp.codelab.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSendMessageButton()

        setupUploadImageButton()

        prepareRecyclerView()

    }

    private fun setupUploadImageButton() {
        uploadImage.setOnClickListener {
            EasyImage.openCameraForImage(this, 0)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagesPicked(imageFiles: MutableList<File>, source: EasyImage.ImageSource?, type: Int) {

                if (imageFiles.isNotEmpty()) {

                    val imageFile = imageFiles[0]
                    val imageUri = Uri.fromFile(imageFile)

                    val storageRef = FirebaseStorage.getInstance().getReference("message_images")
                        .child(System.currentTimeMillis().toString())

                    storageRef.putFile(imageUri)
                        .addOnCompleteListener {
                            it.result?.storage?.downloadUrl?.addOnCompleteListener {
                                val url = it.result.toString()

                                uploadMessage(url, Message.Types.IMAGE)

                            }
                        }


                }

            }


        })

    }


    private fun prepareRecyclerView() {

        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")
        var adapter: MessagesAdapter? = null


        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val messages: MutableList<Message> = mutableListOf()

                snapshot.children.forEach { child ->

                    val message = child.getValue(Message::class.java)

                    message?.let { message ->
                        messages.add(message)
                    }
                }

                messages.reverse()

                adapter = MessagesAdapter(messages)
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, true)
                recyclerView.adapter = adapter

            }

        })

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(newChild: DataSnapshot, p1: String?) {
                val newMessage = newChild.getValue(Message::class.java)
                newMessage?.let { newMessage ->
                    adapter?.newMessage(newMessage)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })


    }

    private fun setupSendMessageButton() {

        sendButton.setOnClickListener {

            if (messageEditText.text.isNotEmpty()) {

                uploadMessage(messageEditText.text.toString(), Message.Types.TEXT)

            } else {
                Toast.makeText(this, "You cannot send an empty message", Toast.LENGTH_SHORT).show()
            }

        }
    }


    fun uploadMessage(text: String, type: String) {

        val messagesRef = FirebaseDatabase.getInstance().getReference("messages")

        val message = Message(text, type)

        messagesRef.push()
            .setValue(message)
            .addOnSuccessListener {
                messageEditText.setText("")
                Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Sending failed!", Toast.LENGTH_SHORT).show()
            }


    }
}
