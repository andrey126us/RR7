package com.example.localizationandformslab;   // Проверь, что пакет совпадает с твоим!

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ========== Настройка ListView ==========
        ListView listView = findViewById(R.id.listViewFurniture);
        // Массив загружается из ресурсов с учётом текущей локали
        String[] furnitureArray = getResources().getStringArray(R.array.furniture_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                furnitureArray
        );
        listView.setAdapter(adapter);

        // Обработчик клика по элементу списка
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Выбрано: " + selected, Toast.LENGTH_SHORT).show();
            }
        });

        // ========== Кнопка перехода к форме регистрации ==========
        Button btnGo = findViewById(R.id.btnGoToRegistration);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}