package com.example.localizationandformslab;  // <--- проверь свой пакет!

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    // Тег согласно заданию
    private static final String TAG = "Registration";

    // TextInputLayout (для setError)
    private TextInputLayout tilFio, tilLogin, tilEmail, tilPhone, tilPassword, tilConfirmPassword, tilBirthDate;
    // EditText
    private TextInputEditText etFio, etLogin, etEmail, etPhone, etPassword, etConfirmPassword, etBirthDate;
    // Кнопки, спиннер, чекбокс
    private Button btnPickDate, btnRegister;
    private Spinner spinnerFurnitureType;
    private CheckBox cbAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        setupSpinner();
        setupDatePicker();
        setupRegisterButton();

        // Открыть диалог даты также по клику на само поле
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void initViews() {
        // Привязка TextInputLayout
        tilFio = findViewById(R.id.tilFio);
        tilLogin = findViewById(R.id.tilLogin);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilBirthDate = findViewById(R.id.tilBirthDate);

        // Привязка EditText
        etFio = findViewById(R.id.etFio);
        etLogin = findViewById(R.id.etLogin);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBirthDate = findViewById(R.id.etBirthDate);

        // Кнопки и прочее
        btnPickDate = findViewById(R.id.btnPickDate);
        btnRegister = findViewById(R.id.btnRegister);
        spinnerFurnitureType = findViewById(R.id.spinnerFurnitureType);
        cbAgree = findViewById(R.id.cbAgree);
    }

    private void setupSpinner() {
        // Адаптер из ресурсов, локализован
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.furniture_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFurnitureType.setAdapter(adapter);

        // Можно обработать выбор, не обязательно
        spinnerFurnitureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ничего
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDatePicker() {
        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                RegistrationActivity.this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format("%02d.%02d.%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etBirthDate.setText(date);
                },
                year, month, day);
        // Не даём выбрать будущую дату
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        // Очистить предыдущие ошибки
        clearErrors();

        String fio = etFio.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        String birthDate = etBirthDate.getText().toString().trim();
        String furnitureType = spinnerFurnitureType.getSelectedItem().toString();
        boolean isAgreed = cbAgree.isChecked();

        boolean isValid = true;

        // 1. ФИО (кириллица, дефис, пробелы)
        if (!fio.matches("^[А-Яа-яЁё\\s-]+$")) {
            showError(tilFio, "Только кириллические буквы, пробелы и дефис");
            Log.w(TAG, "Неверный формат ФИО: " + fio);
            isValid = false;
        }

        // 2. Логин (латиница)
        if (!login.matches("^[A-Za-z]+$")) {
            showError(tilLogin, "Только латинские буквы (строчные и заглавные)");
            Log.w(TAG, "Неверный формат логина: " + login);
            isValid = false;
        }

        // 3. Email
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showError(tilEmail, "Неверный формат email");
            Log.w(TAG, "Неверный формат email: " + email);
            isValid = false;
        }

        // 4. Телефон (международный, регулярка из теории)
        if (!phone.matches("^\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}$")) {
            showError(tilPhone, "Формат: +7XXXXXXXXXX или +7 (XXX) XXX-XX-XX");
            Log.w(TAG, "Неверный формат телефона: " + phone);
            isValid = false;
        }

        // 5. Пароль: минимум 6, цифра, заглавная буква
        if (password.length() < 6) {
            showError(tilPassword, "Минимум 6 символов");
            Log.w(TAG, "Пароль слишком короткий");
            isValid = false;
        } else if (!password.matches(".*\\d.*")) {
            showError(tilPassword, "Должна быть хотя бы одна цифра");
            Log.w(TAG, "В пароле нет цифры");
            isValid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            showError(tilPassword, "Должна быть хотя бы одна заглавная буква");
            Log.w(TAG, "В пароле нет заглавной буквы");
            isValid = false;
        }

        // 6. Повтор пароля
        if (!password.equals(confirmPassword)) {
            showError(tilConfirmPassword, "Пароли не совпадают");
            Log.w(TAG, "Пароли не совпадают");
            isValid = false;
        }

        // 7. Дата рождения: формат + ограничения по году и будущему
        if (!birthDate.matches("^(0[1-9]|[12]\\d|3[01])\\.(0[1-9]|1[0-2])\\.(19|20)\\d{2}$")) {
            showError(tilBirthDate, "Формат: ДД.ММ.ГГГГ");
            Log.w(TAG, "Неверный формат даты: " + birthDate);
            isValid = false;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            sdf.setLenient(false);
            try {
                Date parsedDate = sdf.parse(birthDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(parsedDate);
                if (cal.get(Calendar.YEAR) < 1900) {
                    showError(tilBirthDate, "Год не может быть ранее 1900");
                    Log.w(TAG, "Год ранее 1900: " + birthDate);
                    isValid = false;
                } else if (parsedDate.after(new Date())) {
                    showError(tilBirthDate, "Дата не может быть позже сегодняшнего дня");
                    Log.w(TAG, "Дата в будущем: " + birthDate);
                    isValid = false;
                }
            } catch (ParseException e) {
                showError(tilBirthDate, "Некорректная дата");
                Log.w(TAG, "Ошибка парсинга даты: " + birthDate);
                isValid = false;
            }
        }

        // 8. Spinner (тип мебели)
        if (furnitureType.startsWith("--") || furnitureType.isEmpty()) {
            Toast.makeText(this, "Выберите тип мебели", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Тип мебели не выбран");
            isValid = false;
        }

        // 9. Чекбокс согласия
        if (!isAgreed) {
            Toast.makeText(this, "Необходимо согласие на обработку персональных данных", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Согласие не отмечено");
            isValid = false;
        }

        // Итог
        if (isValid) {
            Toast.makeText(this, "Регистрация успешна", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Успешная регистрация: " +
                    "ФИО=" + fio + ", Логин=" + login + ", Email=" + email +
                    ", Телефон=" + phone + ", Дата=" + birthDate +
                    ", Тип мебели=" + furnitureType);
            // Можно закрыть активность или перейти дальше
        } else {
            Toast.makeText(this, "Исправьте ошибки в форме", Toast.LENGTH_LONG).show();
        }
    }

    private void showError(TextInputLayout layout, String message) {
        layout.setError(message);
    }

    private void clearErrors() {
        tilFio.setError(null);
        tilLogin.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilBirthDate.setError(null);
    }
}