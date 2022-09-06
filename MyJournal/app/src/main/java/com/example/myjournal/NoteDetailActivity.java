package com.example.myjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

public class NoteDetailActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        titleEditText = findViewById(R.id.note_title_text);
        contentEditText = findViewById(R.id.note_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn = findViewById(R.id.delete_note_text_view_btn);

        //RECEIVING DATA
        title = getIntent().getStringExtra("Title");
        content = getIntent().getStringExtra("Content");
        docId = getIntent().getStringExtra("DocId");

        if (docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edit your Notes");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v)-> saveNote());
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase());
    }

    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Please add title");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if (isEditMode){

            //update the notes
            documentReference = Utility.getCollectionReferenceNotes().document(docId);
        }else{
            documentReference = Utility.getCollectionReferenceNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Notes will be added
                    Utility.showToast(NoteDetailActivity.this, "Notes added");
                    finish();
                }else{
                    Utility.showToast(NoteDetailActivity.this, "Notes failed");
                }
            }
        });


    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            documentReference = Utility.getCollectionReferenceNotes().document(docId);
            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Notes is deleted
                    Utility.showToast(NoteDetailActivity.this, "Notes deleted");
                    finish();
                }else{
                    Utility.showToast(NoteDetailActivity.this, "Notes failed while deleting");
                }
            }
        });

    }
}