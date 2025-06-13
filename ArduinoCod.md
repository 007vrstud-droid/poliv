#include <WiFi.h>
#include <ESPAsyncWebServer.h>
#include <ArduinoJson.h>

// Данные Wi-Fi
const char* ssid = "TS2.4_8EEF";
const char* password = "C8C08EEF";

// Сетевые настройки
IPAddress local_IP(192, 168, 1, 10);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);

// Пины сенсоров освещения (теперь 5 штук)
#define LIGHT_SENSOR_PIN_1 34
#define LIGHT_SENSOR_PIN_2 35
#define LIGHT_SENSOR_PIN_3 32
#define LIGHT_SENSOR_PIN_4 33
#define LIGHT_SENSOR_PIN_5 25

// Пины клапанов (LED) — теперь 5 штук
#define LED_PIN_1 13
#define LED_PIN_2 2
#define LED_PIN_3 3
#define LED_PIN_4 4
#define LED_PIN_5 1

// Состояния клапанов (LED)
bool ledState1 = false;
bool ledState2 = false;
bool ledState3 = false;
bool ledState4 = false;
bool ledState5 = false;

// Web-сервер
AsyncWebServer server(80);

// 🔁 Функция управления клапанами (LED)
void handleLedControl(const JsonArray& arr, bool turnOn, AsyncWebServerRequest* request) {
bool invalidLed = false;

for (int ledNum : arr) {
switch (ledNum) {
case 1:
ledState1 = turnOn;
digitalWrite(LED_PIN_1, turnOn ? HIGH : LOW);
break;
case 2:
ledState2 = turnOn;
digitalWrite(LED_PIN_2, turnOn ? HIGH : LOW);
break;
case 3:
ledState3 = turnOn;
digitalWrite(LED_PIN_3, turnOn ? HIGH : LOW);
break;
case 4:
ledState4 = turnOn;
digitalWrite(LED_PIN_4, turnOn ? HIGH : LOW);
break;
case 5:
ledState5 = turnOn;
digitalWrite(LED_PIN_5, turnOn ? HIGH : LOW);
break;
default:
invalidLed = true;
}
}

if (invalidLed) {
request->send(400, "application/json", "{\"error\":\"Invalid valve number. Only 1 to 5 allowed.\"}");
} else {
request->send(200); // OK, без тела
}
}

void setup() {
Serial.begin(115200);

// Настройка пинов клапанов (LED)
pinMode(LED_PIN_1, OUTPUT);
pinMode(LED_PIN_2, OUTPUT);
pinMode(LED_PIN_3, OUTPUT);
pinMode(LED_PIN_4, OUTPUT);
pinMode(LED_PIN_5, OUTPUT);

digitalWrite(LED_PIN_1, LOW);
digitalWrite(LED_PIN_2, LOW);
digitalWrite(LED_PIN_3, LOW);
digitalWrite(LED_PIN_4, LOW);
digitalWrite(LED_PIN_5, LOW);

// Подключение к Wi-Fi
if (!WiFi.config(local_IP, gateway, subnet)) {
Serial.println("Не удалось задать статический IP!");
}

WiFi.begin(ssid, password);
Serial.print("Подключение к Wi-Fi...");
while (WiFi.status() != WL_CONNECTED) {
delay(1000);
Serial.print(".");
}

Serial.println("\nWi-Fi подключён!");
Serial.print("IP-адрес: ");
Serial.println(WiFi.localIP());

// 📡 Показания датчиков освещения
server.on("/light", HTTP_GET, [](AsyncWebServerRequest* request) {
int lightVal1 = analogRead(LIGHT_SENSOR_PIN_1);
int lightVal2 = analogRead(LIGHT_SENSOR_PIN_2);
int lightVal3 = analogRead(LIGHT_SENSOR_PIN_3);
int lightVal4 = analogRead(LIGHT_SENSOR_PIN_4);
int lightVal5 = analogRead(LIGHT_SENSOR_PIN_5);

    StaticJsonDocument<300> jsonDoc;
    jsonDoc["sensor1"] = lightVal1;
    jsonDoc["sensor2"] = lightVal2;
    jsonDoc["sensor3"] = lightVal3;
    jsonDoc["sensor4"] = lightVal4;
    jsonDoc["sensor5"] = lightVal5;

    String response;
    serializeJson(jsonDoc, response);
    request->send(200, "application/json", response);
});

// 💡 Включение клапанов (LED)
server.on("/valves/open", HTTP_POST, [](AsyncWebServerRequest* request) {},
NULL,
[](AsyncWebServerRequest* request, uint8_t* data, size_t len, size_t index, size_t total) {
StaticJsonDocument<200> jsonDoc;
DeserializationError error = deserializeJson(jsonDoc, data, len);

      if (error || !jsonDoc.is<JsonArray>()) {
        request->send(400, "application/json", "{\"error\":\"Expected valid JSON array\"}");
        return;
      }

      JsonArray arr = jsonDoc.as<JsonArray>();
      handleLedControl(arr, true, request);
    }
);

// 💡 Выключение клапанов (LED)
server.on("/valves/close", HTTP_POST, [](AsyncWebServerRequest* request) {},
NULL,
[](AsyncWebServerRequest* request, uint8_t* data, size_t len, size_t index, size_t total) {
StaticJsonDocument<200> jsonDoc;
DeserializationError error = deserializeJson(jsonDoc, data, len);

      if (error || !jsonDoc.is<JsonArray>()) {
        request->send(400, "application/json", "{\"error\":\"Expected valid JSON array\"}");
        return;
      }

      JsonArray arr = jsonDoc.as<JsonArray>();
      handleLedControl(arr, false, request);
    }
);

// 📊 Статус клапанов
server.on("/valves/status", HTTP_GET, [](AsyncWebServerRequest* request) {
StaticJsonDocument<300> statusDoc;
statusDoc["1"] = ledState1 ? "on" : "off";
statusDoc["2"] = ledState2 ? "on" : "off";
statusDoc["3"] = ledState3 ? "on" : "off";
statusDoc["4"] = ledState4 ? "on" : "off";
statusDoc["5"] = ledState5 ? "on" : "off";

    String response;
    serializeJson(statusDoc, response);
    request->send(200, "application/json", response);
});

// 🌐 Корень
server.on("/", HTTP_GET, [](AsyncWebServerRequest* request) {
request->send(200, "text/plain", "ESP32 Web Server is running.");
});

// 🚀 Запуск сервера
server.begin();
}

void loop() {
// Не нужен для AsyncWebServer
}
