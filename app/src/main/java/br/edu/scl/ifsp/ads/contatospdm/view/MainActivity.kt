package br.edu.scl.ifsp.ads.contatospdm.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.ads.contatospdm.R
import br.edu.scl.ifsp.ads.contatospdm.databinding.ActivityMainBinding
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.EXTRA_CONTACT
import br.edu.scl.ifsp.ads.contatospdm.model.Contact
import org.apache.http.params.CoreConnectionPNames

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //Data source
    private val contactList: MutableList<Contact> = mutableListOf()
    //Adapter
    private  val contactAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            contactList.map { contact ->
                contact.name
            }
        )
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        //fillContacts()
        amb.contatosLv.adapter = contactAdapter

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result ->
            //se o user clicar em aceite vou extrair um contact
            if (result.resultCode == RESULT_OK){
                                        // como se fosse um getContacts
                val contact = result.data?.getParcelableExtra<Contact>(EXTRA_CONTACT)
                contact?.let {_contact ->
                    contactList.add(_contact)
                    contactAdapter.add(_contact.name)
                    //comando que avisa quando um contato novo eh adicionado
                    contactAdapter.notifyDataSetChanged()
                }
            }


        }
    }

    //vai tratar os dados do menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
//funcao que trata o evento de click no +
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId){
            R.id.addContactMi -> {
                //Abrir a tela ContactActivity para adicionar
                //um novo contato.
                // uma intent dessa classe para a intent da contactActivity
                carl.launch(Intent(this, ContactActivity::class.java))
                true
            }
            else -> false
        }
    }
    private fun fillContacts() {
        for (i in 1..50) {
            contactList.add(
                Contact(
                    i,
                    "Nome $i",
                    "Endereço $i",
                    "Telefone $i",
                    "Email $i"
                )
            )
        }
    }

}