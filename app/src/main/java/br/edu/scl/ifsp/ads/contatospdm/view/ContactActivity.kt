package br.edu.scl.ifsp.ads.contatospdm.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.ads.contatospdm.databinding.ActivityContactBinding
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.EXTRA_CONTACT
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.INVALID_CONTACT_ID
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.VIEW_CONTACT
import br.edu.scl.ifsp.ads.contatospdm.model.Contact
import kotlin.random.Random

class ContactActivity : AppCompatActivity() {
    private val acb: ActivityContactBinding by lazy {
        ActivityContactBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(acb.root)

        setSupportActionBar(acb.toolBarIn.toolbar)
        supportActionBar?.subtitle = "Contact details"

        //recebendo o contato na hora que clica em editar
        val receivedContact = intent.getParcelableExtra<Contact>(EXTRA_CONTACT)

        //se não for nullo..
        receivedContact?.let { _receivedContact ->
            val viewContact = intent.getBooleanExtra(VIEW_CONTACT, false)
            with(acb){
                //find By id
                if(viewContact){
                    nameEt.isEnabled = false
                    addressEt.isEnabled = false
                    phoneEt.isEnabled = false
                    emailEt.isEnabled = false
                    saveBt.visibility = View.GONE
                }
                //
                nameEt.setText(_receivedContact.name)
                addressEt.setText(_receivedContact.address)
                phoneEt.setText(_receivedContact.phone)
                emailEt.setText(_receivedContact.email)
            }

            // se eh para visualizar contato
        }


        with(acb){
            saveBt.setOnClickListener {
                val contact: Contact = Contact(
                    id = receivedContact?.id,
                    name = nameEt.text.toString(),
                    address = addressEt.text.toString(),
                    phone = phoneEt.text.toString(),
                    email = emailEt.text.toString()
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_CONTACT, contact)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
    //private fun generateId(): Int = Random(System.currentTimeMillis()).nextInt()
}