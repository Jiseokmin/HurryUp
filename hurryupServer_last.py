import socket
import subprocess
import pyfirmata
import threading
import time
from pygame import mixer # Load the required library
from _thread import *

import json
import math
import os
import numpy as np
import signal


def disconnect():
    # subprocess.call('sudo poweroff', shell=True)
    return 'socket disconnected'

def connect_to_arduino():
    try:
        PORT = 'COM8'
        board = pyfirmata.Arduino(PORT)
        pin6 = board.get_pin('d:6:p')
        return 'Connected to Arduino successfully!', PORT, board, pin6
    except:
        return 'Failed to connect to Arduino.', '', '', ''


def disable_posture_check(postureCheck):
    postureCheck = False
    return 'disable posture check', postureCheck

def toggle_posture_check(postureCheck):
    if (postureCheck):
        postureCheck = False
        return 'disable posture check', postureCheck
    else:
        postureCheck = True
        return 'enable posture check', postureCheck




def set_sound_volume(volume):
    global mixer
    soundVolume = float(volume)
    mixer.music.set_volume(soundVolume/10)
    
    return 'set sound volume '+volume, soundVolume

def set_vibration_strength(strength):
    vibrationStrength = float(strength)
    
    return 'set vibration strength '+strength, vibrationStrength

def set_correction_sensitivity(sensitivity):
    correctionSensitivity = sensitivity
    return 'set sensitivity '+sensitivity, correctionSensitivity

def terminate_server():
    return




## ============= OpenPose ================ ##
'''
구현 리스트
1. 컴퓨터와의 통신 구현
5. 목 허리 각도 계산 다시 
'''

def file_open(file_path, number, file_ex):
    # 파일이 존재하는지 확인
    ex = False
    file_list = os.listdir(file_path)
    file_list.sort()

    for target in file_list:
        if target == number+file_ex:
            ex = True
            break
        else:
            ex = False

    return ex


def clear_folder(file_path, count, file_ex):
    file_list = os.listdir(file_path)
    file_list.sort()

    if len(file_list) == 0:
        pass
    else:
        try:
            for d in file_list:
                os.remove(file_path + count + file_ex)
                count = up_count(count)
        except FileNotFoundError:
            print("FileNotFoundError")
            return search_count(file_path)

    return count

def search_count(file_path):
    file_list = os.listdir(file_path)
    file_list.sort()

    try:
        file_name = file_list[0]
        count = file_name.split('_')[0]
    except IndexError:
        print("IndexError")
        count = 0

    return count


def extract_point_list(file_path, number, file_ex):

    with open(file_path + number + file_ex) as json_file:
        try:
            json_data = json.load(json_file) ## Error Point
        except:
            print("Except")
            return []

    if json_data["people"] != []:
        people_dict = json_data["people"][0]
        point_list = people_dict["pose_keypoints_2d"]
    else:
        point_list = []

    return point_list


def make_list(point_list):
    pair_list = []
    temp_list = []
    n = 0

    for i, v in enumerate(point_list):
        if (n == 0):
            temp_list.append(v)
            n += 1
        elif (n == 1):
            temp_list.append(v)
            n += 1
        elif (n == 2):
            pair_list.append(temp_list)
            temp_list = []
            n = 0
    
    return pair_list


def cal_neck_vector(pair_list):  # 거북목 여부 계산
    global correctionSensitivity

    if (pair_list[0][0] == 0 or pair_list[0][1] == 0 or pair_list[1][0] == 0 or pair_list[1][1] == 0):
        return "Recognize Error"
    
    unit_vec = np.array([0, 1])
    neck_vec = np.array([pair_list[0][0] - pair_list[1][0], pair_list[0][1] - pair_list[1][1]])
    #ear_vec = np.array(pair_list[][] - pair_list[][], pair_list[][] - pair_list[][])
    cos = sum(unit_vec * neck_vec) / math.sqrt(sum((unit_vec - neck_vec) ** 2))
    angle = math.degrees(math.acos(cos))
    max_ = 140
    min_ = 120
    criteria_angle = min_ + ((max_ - min_) / 10)*float(correctionSensitivity)

    # 실험을 통해 적정 각도 계속 확인
    if (angle > criteria_angle):
        neck_pose = "Normal"
    else:
        neck_pose = "Turtle_neck"
    print("neck_angle :", angle)
    return neck_pose


def cal_waist_vector(pair_list):  # 굽은허리 여부 계산
    global correctionSensitivity

    if (pair_list[8][0] == 0 or pair_list[8][1] == 0 or pair_list[1][0] == 0 or pair_list[1][1] == 0):
        return "Recognize Error"
    
    unit_vec = np.array([0, 1])
    waist_vec = np.array([pair_list[1][0] - pair_list[8][0], pair_list[1][1] - pair_list[8][1]])
    cos = sum(unit_vec * waist_vec) / math.sqrt(sum((unit_vec - waist_vec) ** 2))
    angle = math.degrees(math.acos(cos))
    
    max_ = 170
    min_ = 150
    criteria_angle = min_ + ((max_ - min_) / 10)*float(correctionSensitivity)

    # criteria_angle = 44.80 - 0.04/10*(correctionSensitivity - 10)

    # 실험을 통해 적정 각도 계속 확인
    if (angle > criteria_angle):
        waist_pose = "Normal"
    else:
        waist_pose = "Bent_waist"
    print("waist_angle :", angle)
    return waist_pose


def recog_pose(point_list):

    pair_list = make_list(point_list)
    neck_state = cal_neck_vector(pair_list)
    waist_state = cal_waist_vector(pair_list)

    return neck_state, waist_state


def up_count(number):
    num_list = list(number)
    num1 = np.array(num_list, dtype=np.int32)
    add = np.array([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1])
    result = num1 + add
    for idx, val in enumerate(reversed(result)):
        if idx == 11:
            break
        if val >= 10:
            result[11 - idx] = result[11 - idx] % 10
            result[10 - idx] += 1
    empty = ""

    for element in result.tolist():
        empty += str(element)

    return empty



def turn_vibration(state):
    global board
    global vibrationStrength
    if (board != ''):
        if state:
            board.digital[6].write(vibrationStrength)
        else:
            board.digital[6].write(0)

def turnon_sound(message):
    global mixer
    if (message == 0):
        mixer.music.load('turtle_neck.wav')
    else:
        mixer.music.load('waist_curve.wav')
    mixer.music.play()

def turnoff_sound():
    global mixer
    mixer.music.stop()

def alarm_to_user(neck, waist):
    if (waist == "Bent_waist"):
        turn_vibration(True)
        turnon_sound(1) # 허리
        print("neck_state : " + waist)
        return
    elif (neck == "Turtle_neck"):
        turn_vibration(True)
        turnon_sound(0) # 거북목
        print("neck_state : " + neck)
        return
    else:
        turn_vibration(False)
        # turnoff_sound()



class StartOpenPose(object):
    global runningProgram
    global rasp_ip

    def __init__(self, interval=5): # run thread every (interval) seconds
        global openposeThread
        self.interval = interval

        openposeThread = threading.Thread(target=self.run, args=())
        openposeThread.daemon = True # Daemonize thread
        openposeThread.start() # Start the execution

    def run(self):
        global runningProgram
        command = 'D:/Study/Capstone/openpose/openpose-1.4.0-win64-gpu-binaries/bin/OpenPoseDemo.exe --ip_camera http://'+rasp_ip+':8080/stream/video.mjpeg --number_people_max 1 --write_json output/'
        process = subprocess.call(command, shell=True)
        print('Terminate OpenPose')
        runningProgram = False
        exit()



class ReadJsonFile(object):
    global openposeThread
    global runningProgram

    def __init__(self, interval=0.1): # run thread every (interval) seconds
        self.interval = interval
        self.count = "000000000000"
        self.file_path = "D:/Study/Capstone/openpose/openpose-1.4.0-win64-gpu-binaries/output/"
        self.file_ex = "_keypoints.json"

        readThread = threading.Thread(target=self.run, args=())
        readThread.daemon = True # Daemonize thread
        readThread.start() # Start the execution

    def pose(self, count, file_path, file_ex):

        print(count)
        while runningProgram:
            if file_open(file_path, count, file_ex):
                break

        if (runningProgram):
            point_list = extract_point_list(file_path, count, file_ex)

            if point_list != []:
                state = recog_pose(point_list)

                if state == ('normal','normal'):
                    os.remove(file_path + count + file_ex)
                    next_count = up_count(count)
                else:
                    # sound and vibrate
                    if (postureCheck):
                        alarm_to_user(state[0], state[1]) # 알림 (neck, waist)
                    next_count = clear_folder(file_path, count, file_ex)
                
            else:
                state = ("recognize Error", "recognize Error")
                if (postureCheck):
                    alarm_to_user(state[0], state[1]) # 알림 (neck, waist)
                    print("(neck_state, waist_state) = ", state)
                try:
                    os.remove(file_path + count + file_ex)
                except PermissionError:
                    pass
                next_count = up_count(count)


            for idx, val in enumerate(count):
                count[idx].replace(val, next_count[idx])
            
            return next_count
        else:
            return "000000000000"
            

    def run(self):
        self.count = "000000000000"
        clear_folder(self.file_path, search_count(self.file_path), self.file_ex)

        while runningProgram: # run forever until the application exits
            self.count = self.pose(self.count, self.file_path, self.file_ex)
            time.sleep(self.interval)
        
        print('Terminate ReadJsonFile')
        os._exit(1)
        #process.terminate()




#### ==== Settings ==== ####
serverPort = 8888
default_rasp_ip = '10.210.60.78' # '10.210.61.42'

rasp_ip = input("라즈베리 파이 IP를 입력하세요 : ")
if (rasp_ip == ""):
    rasp_ip = default_rasp_ip
    print("라즈베리 파이 주소: "+str(rasp_ip))

mixer.init() # 소리
playing_sound = False
arduino_connection, PORT, board, pin6 = connect_to_arduino()
print(arduino_connection)


serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
serverSocket.bind(('', serverPort))
serverSocket.listen(1)

print("The server is ready to receive on port", serverPort)

streamingStatus = False
vibrationStrength = 0.6
correctionSensitivity = 5.0
soundVolume = 5.0
runningProgram = True
postureCheck = False



#### ==== Main Server ==== ####
readThread = ReadJsonFile() # Read Json File Thread
openposeThread = StartOpenPose() # Start OpenPose Thread



while True:
    try:
        (connectionSocket, clientAddress) = serverSocket.accept()
        print('Connection requested from', clientAddress)

        while True:
            try:
                message = connectionSocket.recv(2048)
            except KeyboardInterrupt:
                break

            if not message:
                continue

            modifiedMessage = message.decode()
            modifiedMessage = modifiedMessage.rstrip()
            modifiedMessage = modifiedMessage.rstrip('\r')
            modifiedMessage = modifiedMessage.rstrip('\n')

            if not modifiedMessage:
                replyMessage = 'Error!: Invalid Command'

            elif (modifiedMessage == 'exit'):
                replyMessage = disconnect()
                break

            elif (modifiedMessage == 'connect to server'):
                replyMessage = rasp_ip

            elif (modifiedMessage == 'connect to arduino'):
                replyMessage, PORT, board, pin6, engine = connect_to_arduino()
            
            elif (modifiedMessage == 'disable posture check'): # Toggle Posture Check
                replyMessage, postureCheck = disable_posture_check(postureCheck)
            
            elif (modifiedMessage == 'toggle posture check'): # Toggle Posture Check
                replyMessage, postureCheck = toggle_posture_check(postureCheck)
                break
            
            elif (modifiedMessage == 'toggle camera'): # Toggle Camera
                replyMessage, streamingStatus = toggle_camera(streamingStatus)

            elif (modifiedMessage[:18] == 'vibration strength'):
                replyMessage, vibrationStrength = set_vibration_strength(modifiedMessage[19:])

            elif (modifiedMessage[:12] == 'sound volume'):
                replyMessage, soundVolume = set_sound_volume(modifiedMessage[13:16])

            elif (modifiedMessage[:22] == 'correction sensitivity'):
                replyMessage, correctionSensitivity = set_correction_sensitivity(modifiedMessage[23:26])
            
            else:
                replyMessage = 'Error!: Unknown Command - '+modifiedMessage
                
            replyMessage += '\n'
            connectionSocket.send(replyMessage.encode())
            print(replyMessage)

        replyMessage += '\n'
        connectionSocket.send(replyMessage.encode())
        print(replyMessage)

        connectionSocket.close()
        print("connection closed")

    except KeyboardInterrupt:
        break


serverSocket.close()
print('Server has terminated')
# openposeThread.exit_thread()
# readThread.exit_thread()


'''
subprocess.check_output : return result
subprocess.call : just call
'''
