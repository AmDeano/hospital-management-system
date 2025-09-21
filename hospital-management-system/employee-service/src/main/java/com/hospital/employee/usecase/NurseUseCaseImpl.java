package com.hospital.employee.usecase;

import com.hospital.employee.entity.Nurse;
import com.hospital.employee.repository.NurseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NurseUseCaseImpl extends AbstractEmployeeUseCase<Nurse> {

    private final NurseRepository nurseRepository;

    public NurseUseCaseImpl(NurseRepository nurseRepository) {
        super(nurseRepository);
        this.nurseRepository = nurseRepository;
    }

    @Override
    protected void updateSpecificFields(Nurse existing, Nurse update) {
        if (update.getShift() != null) existing.setShift(update.getShift());
        if (update.getNursingLicense() != null) existing.setNursingLicense(update.getNursingLicense());
    }

    public List<Nurse> findByShift(String shift) {
        return nurseRepository.findByShift(shift);
    }

    public List<Nurse> findActiveByShift(String shift) {
        return nurseRepository.findActiveNursesByShift(shift);
    }
}
