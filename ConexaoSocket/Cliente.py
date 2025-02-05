import socket
import cv2
import numpy as np
from ultralytics import YOLO
import json 
import struct

# Carregar modelo YOLOv8
model = YOLO("yolov8n.pt")

def detect_objects(image_path):
    image = cv2.imread(image_path)
    results = model(image)[0]  # Executa a detecção
    
    detected_objects = []
    for box in results.boxes:
        x1, y1, x2, y2 = map(int, box.xyxy[0])  # Coordenadas do objeto
        class_id = int(box.cls[0])  # ID da classe detectada
        confidence = float(box.conf[0])  # Confiança da detecção
        class_name = results.names[class_id]  # Nome do objeto

        detected_objects.append({
            "name": class_name,
            "x": x1,
            "y": y1,
            "width": x2 - x1,
            "height": y2 - y1,
            "confidence": round(confidence, 2)
        })
    
    return detected_objects

HOST = '127.0.0.1'
PORT = 5000

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
    client_socket.connect((HOST, PORT))

    # Receber imagem do servidor
    image_data = bytearray()
    while chunk := client_socket.recv(4096):
        image_data.extend(chunk)

    # Salvar imagem
    image_path = "received_image.jpg"
    with open(image_path, "wb") as f:
        f.write(image_data)

    # Processar imagem
    detected_objects = detect_objects(image_path)

    # Converter JSON para string e pegar o tamanho
    data_str = json.dumps(detected_objects)
    data_bytes = data_str.encode()
    length_prefix = struct.pack("!I", len(data_bytes))  # Prefixo de 4 bytes com o tamanho

    # Enviar tamanho + dados
    client_socket.sendall(length_prefix + data_bytes)
    
    # Fechar o socket
    client_socket.shutdown(socket.SHUT_WR)
    client_socket.close()
