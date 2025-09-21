package com.hospital.employee.usecase;

import com.hospital.employee.entity.Receptionist;
import com.hospital.employee.repository.ReceptionistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ReceptionistUseCaseImpl extends AbstractEmployeeUseCase<Receptionist> {

    private final ReceptionistRepository receptionistRepository;

    public ReceptionistUseCaseImpl(ReceptionistRepository receptionistRepository) {
        super(receptionistRepository);
        this.receptionistRepository = receptionistRepository;
    }

    @Override
    protected void updateSpecificFields(Receptionist existing, Receptionist update) {
        if (update.getDeskNumber() != null) existing.setDeskNumber(update.getDeskNumber());
    }

    public Optional<Receptionist> findByDeskNumber(String deskNumber) {
        return receptionistRepository.findByDeskNumber(deskNumber);
    }
}
