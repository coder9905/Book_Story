package com.example.booksstory.service.Impl;

import com.example.booksstory.entity.Archive;
import com.example.booksstory.entity.User;
import com.example.booksstory.exception.ResourceBadRequestException;
import com.example.booksstory.exception.ResourceNotFoundException;
import com.example.booksstory.model.Result;
import com.example.booksstory.payload.ArchivePayload;
import com.example.booksstory.payload.UserPayload;
import com.example.booksstory.repository.ArchiveRepository;
import com.example.booksstory.repository.BookRepository;
import com.example.booksstory.repository.RoleRepository;
import com.example.booksstory.repository.UserRepository;
import com.example.booksstory.service.ArchiveService;
import com.example.booksstory.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepository archiveRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public List<ArchivePayload> saveArchive(@RequestBody List<ArchivePayload> payloads){
        try {
            if (payloads != null && payloads.size()>=0){
                return payloads.stream().peek(archivePayload -> {
                    Archive archive=new Archive();
                    archive.setUsers(Arrays.asList(userRepository.findById(archivePayload.getUserId()).orElseThrow(()->new ResourceNotFoundException("user not found"))));
                    archive.setBooks(Arrays.asList(bookRepository.findById(archivePayload.getBookId()).orElseThrow(()->new ResourceNotFoundException("book not found"))));
                    archiveRepository.save(archive);
                }).collect(Collectors.toList());
            }throw new ResourceBadRequestException("send properly body");
        }catch (Exception e){
            log.error("save archive error",e.getMessage());
        }
        return null;
    }

    //Men o'zgartirdim
    
    @Override
    public ResponseEntity<?> getByBookId(Long id){
        try {
            List<ArchivePayload> archivePayloads=archiveRepository.getAllByArchive(id);
            return ResponseEntity.ok(archivePayloads);
        }catch (Exception e){
            log.error("error archive",e.getMessage());
            return new ResponseEntity(new Result(false,"no succesfull",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getByIdArchive(Long id){
        try {
            Archive archive=archiveRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("archive not found"));
            return ResponseEntity.ok(archive);
        }catch (Exception e){
            log.error("error archive",e.getMessage());
            return new ResponseEntity(new Result(false,"no succesfull",null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
