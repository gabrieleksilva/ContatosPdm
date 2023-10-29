package br.edu.scl.ifsp.ads.contatospdm.view

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.ads.contatospdm.R
import br.edu.scl.ifsp.ads.contatospdm.adapter.ContactAdapter
import br.edu.scl.ifsp.ads.contatospdm.controller.ContactController
import br.edu.scl.ifsp.ads.contatospdm.controller.ContactRoomController
import br.edu.scl.ifsp.ads.contatospdm.databinding.ActivityMainBinding
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.EXTRA_CONTACT
import br.edu.scl.ifsp.ads.contatospdm.model.Constant.VIEW_CONTACT
import br.edu.scl.ifsp.ads.contatospdm.model.Contact

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    //Data source
    private val contactList: MutableList<Contact> = mutableListOf()

    //Controller
    private val contactController: ContactRoomController by lazy {
        ContactRoomController(this)
    }

    //Adapter
    private val contactAdapter: ContactAdapter by lazy{
        ContactAdapter(
            this,
            contactList
        )
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolBarIn.toolbar)

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
                    //se o contato existe na linha substitui
                   if(contactList.any{ it.id == _contact.id }) {
                       contactController.editContact(_contact)
                   } else{ //se nao, cria um novo id
                       contactController.insertContact(_contact)
                   }
                }
            }
        }
        //findById
        amb.contatosLv.setOnItemClickListener { adapterView, view, position, l ->
            val contact = contactList[position]
            val viewContactIntent = Intent(this, ContactActivity::class.java)
            viewContactIntent.putExtra(EXTRA_CONTACT, contact)
            viewContactIntent.putExtra(VIEW_CONTACT, true)
            // carregando o contato para outra tela
            startActivity(viewContactIntent)
        }
        registerForContextMenu(amb.contatosLv)
        contactController.getContacts()
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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterContextMenuInfo).position
        val contact = contactList[position]
        return when (item.itemId){
            R.id.removeContactMi -> {
                contactController.removeContact(contact)
                Toast.makeText(this, "Contract removed.", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.editContactMi -> {

                val editContactIntent = Intent(this, ContactActivity::class.java)

                editContactIntent.putExtra(EXTRA_CONTACT, contact)
                // carregando o contato para outra tela
                carl.launch(editContactIntent)
                true
            }
            else -> { false }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(amb.contatosLv)
    }

    fun updateContactList(_contactList: MutableList<Contact>){
        contactList.clear()
        contactList.addAll(_contactList)
        contactAdapter.notifyDataSetChanged()
    }
}