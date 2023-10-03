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

        fillContacts()
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
                   if(contactList.any{ it.id == contact.id}) {
                       val position = contactList.indexOfFirst { it.id == _contact.id }
                       contactList[position] = _contact
                   } else{ //se nao, cria um novo id
                       contactList.add(_contact)
                   }
                    //comando que avisa quando um contato novo eh adicionado
                    contactAdapter.notifyDataSetChanged()
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
        //forma kotlin de find by id: criando uma classe anonima para criar um unico objeto
//        amb.contatosLv.onItemClickListener = object: OnItemClickListener{
//            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                TODO("Not yet implemented")
//            }
//        }
        registerForContextMenu(amb.contatosLv)
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
        return when (item.itemId){
            R.id.removeContactMi -> {
                contactList.removeAt(position)
                contactAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Contract removed.", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.editContactMi -> {
                val contact = contactList[position]
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

}