package dev.shreyaspatil.firebase.coroutines.ui.main

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.shreyaspatil.firebase.coroutines.State
import dev.shreyaspatil.firebase.coroutines.databinding.ActivityMainBinding
import dev.shreyaspatil.firebase.coroutines.model.Post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var language: String

    // Coroutine Scope
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainViewModelFactory())
            .get(MainViewModel::class.java)

        binding.buttonLoad.setOnClickListener(this)
        binding.buttonAdd.setOnClickListener(this)
        binding.buttonLoadEnglish.setOnClickListener(this)
        binding.buttonLoadFrench.setOnClickListener(this)

    }

    private suspend fun loadPosts() {
        viewModel.getAllPosts().collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                }

                is State.Success -> {
                    val postText = state.data.joinToString("\n") {
                        "${it.postContent} ~ ${it.postAuthor} ~ ${it.postLanguage}"
                    }

                    binding.textPostContent.text = postText
                }

                is State.Failed -> showToast("Failed! ${state.message}")
            }
        }
    }

    private suspend fun loadEnglishPosts() {
        viewModel.getEnglishPosts().collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                }

                is State.Success -> {
                    val postText = state.data.joinToString("\n") {
                        "${it.postContent} ~ ${it.postAuthor}"
                    }

                    binding.textPostContent.text = postText
                }

                is State.Failed -> showToast("Failed! ${state.message}")
            }
        }
    }

    private suspend fun loadFrenchPosts() {
        viewModel.getFrenchPosts().collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                }

                is State.Success -> {
                    val postText = state.data.joinToString("\n") {
                        "${it.postContent} ~ ${it.postAuthor}"
                    }

                    binding.textPostContent.text = postText
                }

                is State.Failed -> showToast("Failed! ${state.message}")
            }
        }
    }


    private suspend fun addPost(post: Post) {
        viewModel.addPost(post).collect { state ->
            when (state) {
                is State.Loading -> {
                    showToast("Loading")
                    binding.buttonAdd.isEnabled = false
                }

                is State.Success -> {
                    showToast("Posted")
                    binding.fieldPostContent.setText("")
                    binding.fieldPostAuthor.setText("")
                    binding.radioButtonEnglish.setChecked(false)
                    binding.radioButtonFrench.setChecked(false)
                    binding.buttonAdd.isEnabled = true
                }

                is State.Failed -> {
                    showToast("Failed! ${state.message}")
                    binding.buttonAdd.isEnabled = true
                }
            }
        }
    }

    fun onRadioButtonClicked(v: View?) {
        if (v is RadioButton) {
            val checked = v.isChecked

            when (v!!.id) {
                binding.radioButtonEnglish.id ->
                    if (checked) {
                        language = "English"
                    }
                binding.radioButtonFrench.id ->
                    if (checked) {
                        language = "French"
                    }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.buttonLoad.id -> {
                uiScope.launch {
                    loadPosts()
                }
            }

            binding.buttonLoadEnglish.id -> {
                uiScope.launch {
                    loadEnglishPosts()
                }
            }

            binding.buttonLoadFrench.id -> {
                uiScope.launch {
                    loadFrenchPosts()
                }
            }

            binding.buttonAdd.id -> {
                uiScope.launch {
                    addPost(
                        Post(
                            postContent = binding.fieldPostContent.text.toString(),
                            postAuthor = binding.fieldPostAuthor.text.toString(),
                            postLanguage = language
                        )
                    )
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}
