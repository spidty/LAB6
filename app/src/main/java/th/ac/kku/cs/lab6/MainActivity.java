package th.ac.kku.cs.lab6;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private StudentAdapter mAdapter;
    private List<Student> studentList = new ArrayList<>();
    private RecyclerView studentRecyclerView;
    private Button addButton;

    private DatabaseHelper db;

    private void createStudent(String id, String name){
        db.insertStudent(id, name);
        Student student = db.getStudent(id);
        if (student != null){
            studentList.add(0, student);
        }
    }

    private void updateStudent(String id, String name, int position){
        Student student = studentList.get(position);

        student.setId(id);
        student.setName(name);

        db.updateSudent(student);

        studentList.set(position, student);
        mAdapter.notifyItemChanged(position);
    }

    private void deleteStudent(int position){
        db.deleteStudent(studentList.get(position));

        studentList.remove(position);
        mAdapter.notifyItemChanged(position);
    }

    private void showStudentDialog(final boolean shouldUpdate, final Student student,final int position){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.student_dialog,null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputId = view.findViewById(R.id.text_view_id);
        final EditText inputName = view.findViewById(R.id.text_view_name);
        TextView dialogTitel = view.findViewById(R.id.text_view_dialog_title);
        dialogTitel.setText(!shouldUpdate ? "New Student" : "Edit Student");

        if (shouldUpdate && student != null){
            inputId.setText(student.getId());
            inputName.setText(student.getName());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id)  {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(inputId.getText().toString())) {
                    Toast.makeText(MainActivity.this,"Enter ID!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                if (shouldUpdate && student != null) {
                    updateStudent(inputId.getText().toString(),
                            inputName.getText().toString(),
                            position);
                } else  {
                    createStudent(inputId.getText().toString(), inputName.getText().toString());
                }
            }
        });
    }

    private void showActionDialog(final int position){
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choise option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    showStudentDialog(true, studentList.get(position), position);
                } else  {
                    deleteStudent(position);
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.button_add);
        studentRecyclerView = findViewById(R.id.recycler_view_student);

        db = new DatabaseHelper(this);
        studentList.addAll(db.getAllStudents());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStudentDialog(false, null, -1);
            }
        });

        mAdapter = new StudentAdapter(this, studentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        studentRecyclerView.setLayoutManager(mLayoutManager);
        studentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        studentRecyclerView.setAdapter(mAdapter);

        studentRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) new RecyclerTouchListener(this,
                studentRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) { showActionDialog(position);}
        }));
    }

}

