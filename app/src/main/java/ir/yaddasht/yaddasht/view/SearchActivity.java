package ir.yaddasht.yaddasht.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import ir.yaddasht.yaddasht.R;
import ir.yaddasht.yaddasht.view.adapter.NoteAdapter;
import ir.yaddasht.yaddasht.model.roomdb.Note;
import ir.yaddasht.yaddasht.viewmodel.NoteViewModel;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;

    private ConstraintLayout emptyView;

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        forceRTLIfSupported();

        emptyView = findViewById(R.id.empty_state_view);

        Toolbar toolbar = findViewById(R.id.top_toolbar_main);
        toolbar.setNavigationIcon(R.drawable.ic_back_rtl);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SearchView searchView = findViewById(R.id.edit_text_search_s);
        searchView.requestFocus();


        RecyclerView recyclerView = findViewById(R.id.recycler_view_s);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        /////////////////////////////
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                noteViewModel.getSearchNotes("%"+newText+"%").observe(SearchActivity.this, new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        if(notes.size()==0){
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else{
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                        }
                        if(newText.equals(""))
                        {
                            notes=null;
                        }
                        adapter.submitList(notes);
                    }
                });
                return false;
            }
        });

    }


    /////
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }
}