package campalans.dam.apptranslate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.viewmodel.CreationExtras
import campalans.dam.apptranslate.API.retrofitService
import campalans.dam.apptranslate.databinding.ActivityMainBinding
import campalans.dam.apptranslate.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var alllanguages = emptyList<Language>()
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()
        getLanguages()
    }

    private fun initListener() {
        binding.btnDetectLanguage.setOnClickListener{
            val text = binding.etDescription.toString()
            if (text.isNotEmpty()){
                getTextLanguages(text)
            }
        }
    }

    private fun getTextLanguages(text: String) {
        CoroutineScope(Dispatchers.IO).launch{
            val result = retrofitService.getTextLanguage(text)
            if (result.isSuccessful){
                checkResult(result.body())
            }else{
                ShowError()
            }
        }
    }

    private fun checkResult(detectionResponses: DetectionResponse?) {
        if(detectionResponses != null && !detectionResponses.data.detetcions.isNullOrEmpty()){
            val correctLanguages:List<Detection> = detectionResponses.data.detetcions.filter{it.isReliable}
            if(correctLanguages.isNotEmpty()){

                val languageName = alllanguages.find { it.code == correctLanguages.first().language }

                if (languageName != null){
                    runOnUiThread{
                        Toast.makeText(this,"LANGUAGE IS: ${languageName.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun getLanguages() {
        CoroutineScope(Dispatchers.IO).launch {
            val languages = retrofitService.getLanguages()
            if (languages.isSuccessful){
                alllanguages = languages.body() ?: emptyList()
                showSucces()
            } else{
                ShowError()
            }
        }
    }

    private fun showSucces() {
        runOnUiThread{
            Toast.makeText(this, "Petició Correcte", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ShowError(){
        runOnUiThread{
            Toast.makeText(this, "Error al fer la trucada a la API", Toast.LENGTH_SHORT).show()
        }
    }
}