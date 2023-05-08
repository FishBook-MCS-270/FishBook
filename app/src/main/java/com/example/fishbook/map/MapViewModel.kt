import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.fishbook.localCatchDetails.LocalCatchDetails
import com.example.fishbook.storage.DataRepository

class MapViewModel : ViewModel() {
    private val dataRepository = DataRepository.get()

    val localCatchDetails: LiveData<List<LocalCatchDetails>> =
        dataRepository.getLocalCatchDetails().asLiveData()
}
