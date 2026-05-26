package ${PACKAGE_NAME}.${package}

import org.elaastix.commons.jpa.repository.ElaastixRepository
import org.springframework.stereotype.Repository

@Repository
interface ${class_name}Repository : ElaastixRepository<${class_name}Entity>
