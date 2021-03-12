package uz.pdp.appjparelationships.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {

    final StudentRepository studentRepository;
    final AddressRepository addressRepository;
    final GroupRepository groupRepository;
    final SubjectRepository subjectRepository;

    public StudentController(StudentRepository studentRepository, AddressRepository addressRepository,
                             GroupRepository groupRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
    }

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }


    //4. GROUP OWNER
    @GetMapping("/forGroupOwner/{groupId}")
    public Page<Student> getStudentListForGroupOwner(@PathVariable Integer groupId,
                                                     @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(groupId, pageable);
        return studentPage;
    }

    @PostMapping
    public String save(@RequestBody StudentDto studentDto) {
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        if(!optionalAddress.isPresent())
            return "Address not found!";
        student.setAddress(optionalAddress.get());

        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if(!optionalGroup.isPresent())
            return "Group not found!";
        student.setGroup(optionalGroup.get());

        List<Integer> subjectIdList = studentDto.getSubjectIdList();

        List<Subject> subjectList = new ArrayList<>();

        for (Integer subjectId : subjectIdList) {
            Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
            optionalSubject.ifPresent(subjectList::add);
        }

        student.setSubjects(subjectList);

        studentRepository.save(student);
        return "Student saved!";
    }

    @PutMapping("/{studentId}")
    public String update(@RequestBody StudentDto studentDto,
                         @PathVariable Integer studentId) {

        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if(optionalStudent.isPresent()){

            optionalStudent.get().setFirstName(studentDto.getFirstName());
            optionalStudent.get().setLastName(studentDto.getLastName());

            Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
            if(!optionalAddress.isPresent())
                return "Address not found!";

            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if(!optionalGroup.isPresent())
                return "Group not found!";

            studentRepository.save(optionalStudent.get());
            return "Student updated!";

        }
        return "Student not found!";

    }

    @DeleteMapping("/{studentId}")
    public String delete(@PathVariable Integer studentId) {
        studentRepository.deleteById(studentId);
        return "Student deleted!";
    }




}
