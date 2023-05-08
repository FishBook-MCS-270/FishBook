import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.storage.DataRepository
import com.google.firebase.auth.FirebaseAuth

class MapViewModel : ViewModel() {
    private val dataRepository = DataRepository.get()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val localCatchDetails: LiveData<List<LocalCatchDetails>>? =
        userId?.let { dataRepository.getLocalCatchDetails(it).asLiveData() }
}
