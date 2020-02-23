# ProgrammersLinePlus

---

## 요구사항 외 부가 기능
1. **메모 검색** : 검색 버튼을 누르고 텍스트를 입력하면 제목 과 본문 내용 중 일치하는 글자가 있는 경우를 리스트로 반환해줍니다.
2. **저장 및 수정 날짜와 시간 보여주기** : 최종 수정 시간(또는 저장 시간)을 timestamp형식으로 Room에 저장하고 날짜형식으로 보여줍니다.
3. **슬라이드를 통한 메모 삭제** : 메뉴 버튼에서 뿐만 아니라 RecyclerView에서 아이템을 왼쪽으로 슬라이드해도 삭제 가능합니다.
4. **원본 사진 확인** : 메모 상세 보기 화면에서 이미지를 클릭하면 전체화면으로 원본 사진을 보여줍니다. 그리고 확대와 축소가 가능합니다.

--- 

## 기능 요구사항

### 기능1: 메모리스트
1. 로컬 영역에 저장된 메모를 읽어 리스트 형태로 화면에 표시합니다. **( O )**
2. 리스트에는 메모에 첨부되어있는 이미지의 썸네일, 제목, 글의 일부가 보여집니다. (이미지가 n개일 경우, 첫 번째 이미지가 썸네일이 되어야 함) **( O )**
3. 리스트의 메모를 선택하면 메모 상세 보기 화면으로 이동합니다. **( O )**
4. 새 메모 작성하기 기능을 통해 메모 작성 화면으로 이동할 수 있습니다. **( O )**

### 기능2: 메모 상세 보기
1. 작성된 메모의 제목과 본문을 볼 수 있습니다. **( O )**
2. 메모에 첨부되어있는 이미지를 볼 수 있습니다. (이미지는 n개 존재 가능) **( O )**
3. 메뉴를 통해 메모 내용 편집 또는 삭제가 가능합니다. **( O )**

### 기능3: 메모 편집 및 작성
1. 제목 입력란과 본문 입력란, 이미지 첨부란이 구분되어 있어야 합니다. (글 중간에 이미지가 들어갈 수 있는 것이 아닌, 첨부된 이미지가 노출되는 부분이 따로 존재) **( O )**
2. 이미지 첨부란의 ‘추가' 버튼을 통해 이미지 첨부가 가능합니다. 첨부할 이미지는 다음 중 한 가지 방법을 선택해서 추가할 수 있습니다. 이미지는 0개 이상 첨부할 수 있습니다. 외부 이미지의 경우, 이미지를 가져올 수 없는 경우(URL이 잘못되었거나)에 대한 처리도 필요합니다.
   - 사진첩에 저장되어 있는 이미지 **( O )**
   - 카메라로 새로 촬영한 이미지 **( O )**
   - 외부 이미지 주소(URL) (참고: URL로 이미지를 추가하는 경우, 다운로드하여 첨부할 필요는 없습니다.) **( O )**
3. 편집 시에는 기존에 첨부된 이미지가 나타나며, 이미지를 더 추가하거나 기존 이미지를 삭제할 수 있습니다. **( O )**


## Demo
**try demo on [here](https://appetize.io/app/rpktkyjy0aa57ftahpc29fh5v4?device=nexus5&scale=75&orientation=portrait&osVersion=10.0)!**

실제 기기에서는 잘 동작하는데 데모 링크에서는 첨부한 사진이 보이지 않는 오류가 있음.