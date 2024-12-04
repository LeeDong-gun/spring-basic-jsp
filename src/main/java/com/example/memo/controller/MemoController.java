package com.example.memo.controller;

import com.example.memo.dto.MemoRequestDto;
import com.example.memo.dto.MemoResponseDto;
import com.example.memo.entity.Memo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


// 메모 생성하기
@RestController
@RequestMapping("/memos")
public class MemoController {

    private final Map<Long, Memo> memoList = new HashMap<>();

    @PostMapping
    public ResponseEntity<MemoResponseDto> createMomo(@RequestBody MemoRequestDto dto) {

        // 식별자가 1씩 증가 하도록 만듦
        Long memoId = memoList.isEmpty() ? 1 : Collections.max(memoList.keySet()) + 1;

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(memoId, dto.getTitle(), dto.getContents());

        // Inmemory DB에 Memo 메모
        memoList.put(memoId, memo);

        // ResponseEntity 로 Http 상태를 Created 의 밸류값인 201로 지정해주기
        return new ResponseEntity<>(new MemoResponseDto (memo), HttpStatus.CREATED);
    }

    // 메모 단건 조회하기
    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDto> findMemoById(@PathVariable Long id) {

        // id 받는 객체
        Memo memo = memoList.get(id);

        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<> (new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 메모 목록 조회하기
    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> findAllMemos(){

        // 리스트 초기화
        List<MemoResponseDto> responseList = new ArrayList<>();

        // HashMap<Memo> -> List<MemoResponseDto>
        for (Memo memo : memoList.values()) {
            MemoResponseDto responseDto = new MemoResponseDto(memo);
            responseList.add(responseDto);
        }

        // 스트림을 이용한 코드
//        responseList = memoList.values().stream().map(MemoResponseDto::new).toList();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    // 단건 전체 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateMemoById(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // 필수값 검증
        memo.update(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 단건 제목 수정 API
    @PatchMapping("/{id}")
    public ResponseEntity<MemoResponseDto> updateTitle(
            @PathVariable Long id,
            @RequestBody MemoRequestDto dto
    ) {
        Memo memo = memoList.get(id);

        //NPE 방지
        if (memo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // 필수값 검증
        if (dto.getTitle() == null || dto.getContents() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        memo.updateTitle(dto);

        return new ResponseEntity<>(new MemoResponseDto(memo), HttpStatus.OK);
    }

    // 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@PathVariable Long id) {

        // memoList 의 Key값에 id를 포함하고 있다면
        if (memoList.containsKey(id)) {
            memoList.remove(id);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}



